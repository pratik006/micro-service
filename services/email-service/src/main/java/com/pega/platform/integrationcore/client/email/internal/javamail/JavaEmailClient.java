/*
 * $Id$
 *
 * Copyright (c) 2017  Pegasystems Inc.
 * All rights reserved.
 *
 * This  software  has  been  provided pursuant  to  a  License
 * Agreement  containing  restrictions on  its  use.   The  software
 * contains  valuable  trade secrets and proprietary information  of
 * Pegasystems Inc and is protected by  federal   copyright law.  It
 * may  not be copied,  modified,  translated or distributed in  any
 * form or medium,  disclosed to third parties or used in any manner
 * not provided for in  said  License Agreement except with  written
 * authorization from Pegasystems Inc.
 */

package com.pega.platform.integrationcore.client.email.internal.javamail;

import com.pega.pegarules.priv.ModuleVersion;
import com.pega.platform.integrationcore.client.email.*;
import com.pega.platform.integrationcore.client.email.config.EmailClientConfiguration;
import com.pega.platform.integrationcore.client.email.config.EmailReceiverProfile;
import com.pega.platform.integrationcore.client.email.config.EmailSenderProfile;
import com.pega.platform.integrationcore.internal.IntegrationUtils;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.QDecoderStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import javax.mail.search.FlagTerm;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

public class JavaEmailClient implements EmailClient {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id:$");

	private static final Logger slf4jLog = LoggerFactory.getLogger(JavaEmailClient.class);

	public static final int DEFAULT_PORT_SMTP = 25;
	
	private static final String CHAR_ENCODING_GB2312 = "GB2312";
	private static final String CHAR_ENCODING_GBK = "GBK";
	
	private static boolean decodeStrict = true;
	static {
		try {
			final String s = System.getProperty("mail.mime.decodetext.strict");
			// default to true
			decodeStrict = s == null || !s.equalsIgnoreCase("false");
		} catch (SecurityException secEx) {
			slf4jLog.debug("Unable to determine strictness of email content decoding", secEx);
		}
	}

	private EmailClientConfiguration configuration;
	private Properties properties;
	private Session session;
	private Transport transport;
	private Store store;
	private Folder folder;
	private String senderProtocol;
	private String receiverProtocol;
	private boolean isAuthenticationUsed;
	private boolean isSessionEstablished;
	private boolean isTransportAcquired;
	private boolean isStoreAcquired;
	private boolean isSenderConnected;
	private boolean isSenderDisconnected;
	private boolean isReceiverConnected;
	private boolean isReceiverDisconnected;

	@Override
	public void initialize(EmailClientConfiguration configuration) throws EmailClientException {
		if (configuration == null) {
			throw new EmailClientException("Email client configuration is missing");
		}
		this.configuration = configuration;

		try {
			disconnect();
			cleanUpSession();

			properties = new Properties();
			if (configuration.getSenderProfile() != null) {
				properties.putAll(getSenderProperties());
			}

			if (configuration.getReceiverProfile() != null) {
				properties.putAll(getReceiverProperties());
			}

			initSession();
		} catch (Exception ex) {
			throw new EmailClientException("Unable to initialize email client due to : " + ex.getMessage(), ex);
		}
	}

	private Properties getSenderProperties() {
		Properties props = new Properties();

		EmailSenderProfile senderProfile = configuration.getSenderProfile();
		senderProtocol = senderProfile.getProtocol();
		if (IntegrationUtils.isBlank(senderProtocol)) {
			senderProtocol = "smtp";
		}
		String propPrefix = "mail." + senderProtocol + '.';

		if (senderProfile.isUseSSL()) {
			senderProtocol = senderProtocol + 's';
			propPrefix = "mail." + senderProtocol + '.';

			props.put(propPrefix + "socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put(propPrefix + "socketFactory.fallback", "false");
		}

		props.put("mail.transport.protocol", senderProtocol);

		String emailTimeout = String.valueOf(configuration.getTimeout());
		props.put(propPrefix + "timeout", emailTimeout);
		props.put(propPrefix + "connectiontimeout", emailTimeout);

		props.put(propPrefix + "starttls.enable", String.valueOf(!(configuration.isDisableStartTLS())));

		try {
			String fqdn = java.net.InetAddress.getLocalHost().getCanonicalHostName();
			props.put(propPrefix + "localhost", fqdn);
		} catch (UnknownHostException e) {
			slf4jLog.error("Unable to set fqdn", e);
		}

		props.put(propPrefix + "sendpartial", "true");

		String password = senderProfile.getUserPassword();
		final boolean auth = IntegrationUtils.hasValue(password);
		props.put(propPrefix + "auth", auth);
		isAuthenticationUsed = auth;

		if (!auth) {
			senderProfile.setUserPassword(null);
			senderProfile.setUserId(null);
		}

		props.put(propPrefix + "host", senderProfile.getHost());

		int port = senderProfile.getPort();
		if (0 != port) {
			props.put(propPrefix + "port", port);
		}

		props.putAll(senderProfile.getAdvancedProperties());

		return props;
	}

	private Properties getReceiverProperties() {
		Properties props = new Properties();

		EmailReceiverProfile receiverProfile = configuration.getReceiverProfile();
		receiverProtocol = receiverProfile.getProtocol();
		String propPrefix = "mail." + receiverProtocol + '.';

		if (receiverProfile.isUseSSL()) {
			receiverProtocol = receiverProtocol + 's';
			propPrefix = "mail." + receiverProtocol + '.';

			props.put(propPrefix + "socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put(propPrefix + "socketFactory.fallback", "false");
		}

		String emailTimeout = String.valueOf(configuration.getTimeout());
		props.put(propPrefix + "timeout", emailTimeout);
		props.put(propPrefix + "connectiontimeout", emailTimeout);

		props.put(propPrefix + "starttls.enable", String.valueOf(!(configuration.isDisableStartTLS())));

		if (receiverProtocol.toLowerCase().startsWith("imap")) {
			props.put(propPrefix + "fetchsize", receiverProfile.getFetchSize());
			props.put(propPrefix + "partialfetch", String.valueOf(receiverProfile.getIsPartialFetch()));
		}

		props.put(propPrefix + "host", receiverProfile.getHost());

		int port = receiverProfile.getPort();
		if (0 != port) {
			props.put(propPrefix + "port", port);
		}

		props.putAll(receiverProfile.getAdvancedProperties());

		return props;
	}

	private void initSession() {
		if (session != null) {
			return;
		}
		session = Session.getInstance(properties);

		isSessionEstablished = true;
	}

	private void setSessionDebug(boolean enableDebug, PrintStream debugStream) {
		session.setDebugOut(debugStream);
		session.setDebug(enableDebug);
	}

	private void cleanUpSession() {
		properties = null;
		session = null;
		isSessionEstablished = false;
	}

	@Override
	public boolean connect(boolean enableDebug, PrintStream debugStream) throws EmailClientException {

		try {
			setSessionDebug(enableDebug, debugStream);

			if (configuration.getSenderProfile() != null) {
				connectOutbound();
			}

			if (configuration.getReceiverProfile() != null) {
				connectInbound();
			}

		} catch (Exception ex) {
			throw new EmailClientException("Unable to connect email client due to : " + ex.getMessage(), ex);
		}
		return true;
	}

	private void connectOutbound() throws MessagingException {
		try {
			connectOutboundInternal();
			slf4jLog.debug("Sender connection established");
		} catch (AuthenticationFailedException ex) {
			if (isSMTPAuthFallback(ex.getMessage())) {
				slf4jLog.debug("Falling back to SMTP no-auth mode session");
				EmailSenderProfile senderProfile = configuration.getSenderProfile();
				senderProfile.setUserId(null);
				senderProfile.setUserPassword(null);
				isAuthenticationUsed = false;
				properties.put("mail." + senderProtocol + ".auth", false);

				reinitiateSession();

				connectOutboundInternal();
				slf4jLog.debug("Sender connection established");
			} else {
				throw ex;
			}
		}
	}

	private void connectOutboundInternal() throws MessagingException {
		EmailSenderProfile profile = configuration.getSenderProfile();
		transport = session.getTransport(senderProtocol);
		isTransportAcquired = true;

		transport.connect(profile.getHost(), profile.getUserId(), profile.getUserPassword());

		isSenderConnected = true;
	}

	private boolean isSMTPAuthFallback(String message) {
		return configuration.getSenderProfile().isDisableAuthRetry()
				&& configuration.getSenderProfile().getPort() == DEFAULT_PORT_SMTP && message != null
				&& message.toLowerCase().contains("o authentication mechanisms supported");
	}

	private void reinitiateSession() {
		boolean isDebug = session.getDebug();
		PrintStream debugStream = null;
		if (isDebug) {
			debugStream = session.getDebugOut();
		}

		session = null;
		isSessionEstablished = false;
		initSession();
		setSessionDebug(isDebug, debugStream);
	}

	private void connectInbound() throws MessagingException {
		EmailReceiverProfile profile = configuration.getReceiverProfile();
		store = session.getStore(receiverProtocol);
		isStoreAcquired = true;
		store.connect(profile.getHost(), profile.getPort(), profile.getUserId(), profile.getUserPassword());
		String folderName = profile.getFolderName();
		if (StringUtils.isNotBlank(folderName)) {
			folder = store.getFolder(folderName);
			folder.open(Folder.READ_WRITE);
			slf4jLog.debug("Receiver connection established for folder: {}", folderName);
		}
		isReceiverConnected = true;

	}  

	@Override
	public boolean disconnect() {
		boolean disconnected = false;
		try {
			disconnected = disconnectOutbound() && disconnectInbound();
		} catch (Exception e) {
			throw new EmailClientRuntimeException("Unable to disconnect email client due to : " + e.getMessage(), e);
		}
		return disconnected;
	}

	private boolean disconnectOutbound() {
		if (transport != null && transport.isConnected()) {
			try {
				transport.close();
			} catch (Exception e) {
				slf4jLog.error("Failed to disconnect sender", e);
				return false;
			}
		}

		transport = null;
		isTransportAcquired = false;
		isSenderDisconnected = true;
		slf4jLog.debug("Sender is disconnected");
		return true;
	}

	private boolean disconnectInbound() {
		if (folder != null && folder.isOpen()) {
			try {
				folder.close(true);
			} catch (Exception e) {
				slf4jLog.error("Failed to close the folder", e);
				return false;
			}
		}

		if (store != null && store.isConnected()) {
			try {
				store.close();
			} catch (Exception e) {
				slf4jLog.error("Failed to close the store", e);
				return false;
			}
		}

		folder = null;
		store = null;
		isStoreAcquired = false;
		isReceiverDisconnected = true;
		slf4jLog.debug("Receiver is disconnected");
		return true;
	}

	@Override
	public MIMEMessage createMessage(InputStream stream) throws EmailClientException {
		Message message;
		try {
			if (stream != null) {
				message = new MimeMessage(session, stream);
			} else {
				message = new MimeMessage(session);
			}
		} catch (MessagingException ex) {
			throw new EmailClientException("Unable to create MIME message due to : " + ex.getMessage(), ex);
		}
		return new JavaMailMIMEMessage(message);
	}

	@Override
	public MIMEPart createPart() {
		return new JavaMailPart();
	}

	@Override
	public MIMEMultipart createMultipart(String type) {
		return new JavaMailMIMEMultipart(type);
	}

	@Override
	public EmailClientResponse sendEmail(MIMEMessage message, boolean autoDisconnect) throws EmailClientException {
		EmailClientResponse emailClientResponse = new EmailClientResponse();
		try {
			if ((!isSenderConnected) || (!transport.isConnected())) {
				connectOutbound();
			}

			Object obj = message.getWrappedObject();
			if (obj instanceof Message) {
				Message msg = (Message) obj;

				transport.sendMessage(msg, msg.getAllRecipients());

				String messageID = null;
				if (msg instanceof MimeMessage) {
					messageID = ((MimeMessage) msg).getMessageID();
				}
				emailClientResponse.setMessageId(messageID);
				slf4jLog.debug("Message with ID: {}, sent successfully", messageID);
			}
		} catch (Exception ex) {
			throw new EmailClientException("Unable to send email due to : " + ex.getMessage(), ex);
		} finally {
			emailClientResponse.setAuthenticationUsed(isAuthenticationUsed);
			emailClientResponse.setSessionEstablished(isSessionEstablished);
			emailClientResponse.setTransportAcquired(isTransportAcquired);
			emailClientResponse.setConnected(isSenderConnected);

			if (transport instanceof SMTPTransport) {
				final String lastResponse = ((SMTPTransport) transport).getLastServerResponse();
				emailClientResponse.setSMTPResponse(lastResponse);
			}

			if (autoDisconnect) {
				disconnectOutbound();
			}

			emailClientResponse.setDisconnected(isSenderDisconnected);
		}

		return emailClientResponse;
	}

	@Override
	public List<MIMEMessage> retrieveEmails(boolean onlyUnread, boolean autoDisconnect) throws EmailClientException {
		List<MIMEMessage> messageList = Collections.emptyList();
		try {
			if ((!isReceiverConnected) || (!store.isConnected())) {
				connectInbound();
			}

			Message[] messages;

			if (onlyUnread) {
				messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			} else {
				messages = folder.getMessages();
			}
			slf4jLog.debug("Retrieved {} message(s)", messages.length);
			messageList = new ArrayList<>(messages.length);

			UIDFolder uidFolder;
			for (Message message : messages) {
				JavaMailMIMEMessage mimeMessage = new JavaMailMIMEMessage(message);
				if("imap".equalsIgnoreCase(senderProtocol))
				{
					uidFolder = (UIDFolder) folder;
					mimeMessage.setUID(uidFolder.getUID(message));
					mimeMessage.setValidity(uidFolder.getUIDValidity());
				}
				else
				{
					//TODO create unique ID for non-IMAP messages, hashCode() may not work well
					mimeMessage.setUID(message.hashCode());
				}
				messageList.add(mimeMessage);
			}

		} catch (Exception ex) {
			throw new EmailClientException("Unable to retrieve emails due to : " + ex.getMessage(), ex);
		} finally {
			if (autoDisconnect) {
				disconnectInbound();
			}
		}

		return messageList;
	}

	
	@Override
	public MIMEMessage getMessagebyUID(long uid, long validity) throws EmailClientException
	{
		Message msg = null;
		try {
			if ((!isReceiverConnected) || (!store.isConnected())) {
				connectInbound();
			}
			
			if("imap".equalsIgnoreCase(senderProtocol))
			{
				UIDFolder uidFolder = (UIDFolder) folder;
				if(validity == uidFolder.getUIDValidity())
				{
					msg = uidFolder.getMessageByUID(uid);
				}
				else
				{
					throw new EmailClientException("UID validity of folder does not match"); 
				}
			}
			else
			{
				throw new EmailClientException("This folder is not IMAP and does not support retrieving messages by UID.");
			} 
			
			
		} 
		catch (Exception ex) 
		{
			throw new EmailClientException("Unable to recover email due to : " + ex.getMessage(), ex);
		}
		
		if(msg==null)
		{
			return null;
		}
		
		return new JavaMailMIMEMessage(msg);
	}

	@Override
	public EmailClientResponse testConnection(ProfileType profileType) {
		EmailClientResponse emailClientResponse = new EmailClientResponse();
		OutputStream outDebug = new ByteArrayOutputStream();
		PrintStream debugStream = new PrintStream(outDebug);
		String errorMessages;

		try {
			if (ProfileType.SENDER == profileType) {
				if (configuration.getSenderProfile() != null) {

					setSessionDebug(true, debugStream);
					emailClientResponse.setSessionEstablished(isSessionEstablished);

					connectOutbound();
					emailClientResponse.setAuthenticationUsed(isAuthenticationUsed);
					emailClientResponse.setTransportAcquired(isTransportAcquired);
					emailClientResponse.setConnected(isSenderConnected);

					if (transport instanceof SMTPTransport) {
						String lastResponse = ((SMTPTransport) transport).getLastServerResponse();
						emailClientResponse.setSMTPResponse(lastResponse);
					}

					disconnectOutbound();
					emailClientResponse.setDisconnected(isSenderDisconnected);
				} else {
					errorMessages = " Sender configuration is missing. ";
					slf4jLog.error(errorMessages);
					emailClientResponse.setErrorMessage(errorMessages);

				}
			} else if (ProfileType.RECEIVER == profileType) {
				if (configuration.getReceiverProfile() != null) {

					setSessionDebug(true, debugStream);
					emailClientResponse.setSessionEstablished(isSessionEstablished);

					connectInbound();
					emailClientResponse.setStoreAcquired(isStoreAcquired);
					emailClientResponse.setConnected(isReceiverConnected);

					disconnectInbound();
					emailClientResponse.setDisconnected(isReceiverDisconnected);
				} else {
					errorMessages = " Receiver configuration is missing. ";
					slf4jLog.error(errorMessages);
					emailClientResponse.setErrorMessage(errorMessages);
				}
			}
		} catch (Exception ex) {
			errorMessages = " Unable to test email client connectivity due to :" + ex.getMessage();
			slf4jLog.debug(errorMessages, ex);
			emailClientResponse.setErrorMessage(errorMessages);
			return emailClientResponse;

		} finally {
			try {
				outDebug.flush();
			} catch (Exception e) {
				slf4jLog.error("Unable to flush the debug stream", e);
			}
			emailClientResponse.setDebugInfo(outDebug.toString());
			setSessionDebug(false, null);
		}
		return emailClientResponse;
	}
	
	public String decodeText(String etext) throws EmailClientException {
		/*
		 * We look for sequences separated by "linear-white-space". (as per RFC
		 * 2047, Section 6.1) RFC 822 defines "linear-white-space" as SPACE | HT
		 * | CR | NL.
		 */
		String lwsp = " \t\n\r";
		StringTokenizer st;

		/*
		 * First, lets do a quick run thru the string and check whether the
		 * sequence "=?" exists at all. If none exists, we know there are no
		 * encoded-words in here and we can just return the string as-is,
		 * without suffering thru the later decoding logic. This handles the
		 * most common case of unencoded headers efficiently.
		 */
		if (etext.indexOf("=?") == -1) {
			return etext;
		}

		// Encoded words found. Start decoding ...

		st = new StringTokenizer(etext, lwsp, true);
		StringBuilder sb = new StringBuilder(); // decode buffer
		StringBuilder wsb = new StringBuilder(); // white space buffer
		boolean prevWasEncoded = false;
		try {
			while (st.hasMoreTokens()) {
				char c;
				String s = st.nextToken();
				// If whitespace, append it to the whitespace buffer
				if (((c = s.charAt(0)) == ' ') || (c == '\t') || (c == '\r') || (c == '\n')) {
					wsb.append(c);
				} else {
					// Check if token is an 'encoded-word' ..
					String word;
					try {
						word = decodeWord(s);
						// Yes, this IS an 'encoded-word'.
						if (!prevWasEncoded && wsb.length() > 0) {
							// if the previous word was also encoded, we
							// should ignore the collected whitespace. Else
							// we include the whitespace as well.
							sb.append(wsb);
						}
						prevWasEncoded = true;
					} catch (ParseException pex) {
						// This is NOT an 'encoded-word'.
						word = s;
						// possibly decode inner encoded words
						if (!decodeStrict) {
							String dword = decodeInnerWords(word);
							if (dword != word) { //NOSONAR used to exclude false-positive 
								// if a different String object was returned,
								// decoding was done.
								if (prevWasEncoded && word.startsWith("=?")) {
									// encoded followed by encoded,
									// throw away whitespace between
								} else {
									// include collected whitespace ..
									if (wsb.length() > 0) {
										sb.append(wsb);
									}
								}
								// did original end with encoded?
								prevWasEncoded = word.endsWith("?=");
								word = dword;
							} else {
								// include collected whitespace ..
								if (wsb.length() > 0) {
									sb.append(wsb);
								}
								prevWasEncoded = false;
							}
						} else {
							// include collected whitespace ..
							if (wsb.length() > 0) {
								sb.append(wsb);
							}
							prevWasEncoded = false;
						}
					}
					sb.append(word); // append the actual word
					wsb.setLength(0); // reset wsb for reuse
				}
			}
		} catch(Exception ex) {
			throw new EmailClientException("Unable to decode email text ", ex);
		}
		sb.append(wsb); // append trailing whitespace
		return sb.toString();
	}

	private String decodeWord(final String eword) throws ParseException, UnsupportedEncodingException {

		if (eword == null || !eword.startsWith("=?")) // not an encoded word
		{
			throw new ParseException("encoded word does not start with \"=?\": " + eword);
		}

		// get charset
		int start = 2;
		int pos;
		if ((pos = eword.indexOf('?', start)) == -1) {
			throw new ParseException("encoded word does not include charset: " + eword);
		}

		String charset = MimeUtility.javaCharset(eword.substring(start, pos));
		if (charset != null && charset.equalsIgnoreCase(CHAR_ENCODING_GB2312)) {
			charset = CHAR_ENCODING_GBK;
		}

		// get encoding
		start = pos + 1;
		if ((pos = eword.indexOf('?', start)) == -1) {
			throw new ParseException("encoded word does not include encoding: " + eword);
		}

		String encoding = eword.substring(start, pos);

		// get encoded-sequence
		start = pos + 1;
		if ((pos = eword.indexOf("?=", start)) == -1) {
			throw new ParseException("encoded word does not end with \"?=\": " + eword);
		}

		/*
		 * XXX - should include this, but leaving it out for compatibility...
		 * 
		 * if (decodeStrict && pos != eword.length() - 2) throw new
		 * ParseException( "encoded word does not end with \"?=\": " + eword););
		 */
		String word = eword.substring(start, pos);

		InputStream is = null;
		try {
			String decodedWord;
			if (word.length() > 0) {
				// Extract the bytes from word
				ByteArrayInputStream bis = new ByteArrayInputStream(ASCIIUtility.getBytes(word));

				// Get the appropriate decoder
				if (encoding.equalsIgnoreCase("B")) {
					is = new BASE64DecoderStream(bis);
				} else if (encoding.equalsIgnoreCase("Q")) {
					is = new QDecoderStream(bis);
				} else {
					throw new UnsupportedEncodingException("unknown encoding: " + encoding);
				}

				// For b64 & q, size of decoded word <= size of word. So
				// the decoded bytes must fit into the 'bytes' array. This
				// is certainly more efficient than writing bytes into a
				// ByteArrayOutputStream and then pulling out the byte[]
				// from it.
				int count = bis.available();
				byte[] bytes = new byte[count];
				// count is set to the actual number of decoded bytes
				count = is.read(bytes, 0, count);

				// Finally, convert the decoded bytes into a String using
				// the specified charset
				decodedWord = count <= 0 ? "" : new String(bytes, 0, count, charset);
			} else {
				// no characters to decode, return empty string
				decodedWord = "";
			}

			if (pos + 2 < eword.length()) {
				// there's still more text in the string
				String rest = eword.substring(pos + 2);
				if (!decodeStrict) {
					rest = decodeInnerWords(rest);
				}
				decodedWord += rest;
			}
			return decodedWord;

		} catch (UnsupportedEncodingException uex) {
			// explicitly catch and rethrow this exception, otherwise
			// the below IOException catch will swallow this up!
			throw uex;
		} catch (IOException ioex) {
			// Shouldn't happen.
			throw new ParseException(ioex.toString());
		} catch (IllegalArgumentException iex) {
			/*
			 * An unknown charset of the form ISO-XXX-XXX, will cause the JDK to
			 * throw an IllegalArgumentException ... Since the JDK will attempt
			 * to create a classname using this string, but valid classnames
			 * must not contain the character '-', and this results in an
			 * IllegalArgumentException, rather than the expected
			 * UnsupportedEncodingException. Yikes
			 */
			throw new UnsupportedEncodingException(charset);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// ignoring, this should not happen irl
				}
			}
		}
	}

	private String decodeInnerWords(String word) throws UnsupportedEncodingException {
		int start = 0, i;
		StringBuilder buf = new StringBuilder();
		while ((i = word.indexOf("=?", start)) >= 0) {
			buf.append(word.substring(start, i));
			// find first '?' after opening '=?' - end of charset
			int end = word.indexOf('?', i + 2);
			if (end < 0) {
				break;
			}
			// find next '?' after that - end of encoding
			end = word.indexOf('?', end + 1);
			if (end < 0) {
				break;
			}
			// find terminating '?='
			end = word.indexOf("?=", end + 1);
			if (end < 0) {
				break;
			}
			String s = word.substring(i, end + 2);
			try {
				s = decodeWord(s);
			} catch (ParseException pex) {
				// ignore it, just use the original string
			}
			buf.append(s);
			start = end + 2;
		}

		if (start == 0) {
			return word;
		}
		if (start < word.length()) {
			buf.append(word.substring(start));
		}
		return buf.toString();
	}

}
