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

import com.pega.platform.integrationcore.client.email.EmailClientRuntimeException;
import com.pega.platform.integrationcore.client.email.MIMEMessage;
import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.smime.SMIMECapabilitiesAttribute;
import org.bouncycastle.asn1.smime.SMIMECapability;
import org.bouncycastle.asn1.smime.SMIMECapabilityVector;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.*;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.*;

public class JavaMailMIMEMessage implements MIMEMessage {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id:$");
	private Message message;
	private String charset;
	private long uid = -1;
	private long validity = -1;

	public JavaMailMIMEMessage(Message message) {
		this.message = message;
	}

	@Override
	public void setDataHandler(byte[] data, String fileName) {
		try {
			DataSource source = new ByteArrayDataSource(data,
					FileTypeMap.getDefaultFileTypeMap().getContentType(fileName));

			if (message instanceof MimeMessage) {
				((MimeMessage) message).setDataHandler(new DataHandler(source));
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setDataHandler(String data, String type) {
		try {

			DataSource source = new ByteArrayDataSource(data, type);

			if (message instanceof MimeMessage) {
				((MimeMessage) message).setDataHandler(new DataHandler(source));
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

	}

	@Override
	public void setText(String text) {
		try {
			message.setText(text);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setText(String text, String charset, String subtype) {
		if (message instanceof MimeMessage) {
			try {
				((MimeMessage) message).setText(text, charset, subtype);
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}
	
	@Override
	public Object getContentAsObject() {
		try {
			return message.getContent();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getContentAsString() {
		String str = null;
		try {
			final Object content = message.getContent();

			if (content instanceof String) {
				str = (String) content;
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return str;
	}

	@Override
	public MIMEMessage getContentAsMimeMessage() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentID(String contentID) {
		if (message instanceof MimeMessage) {
			try {
				((MimeMessage) message).setContentID(contentID);
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public String getContentID() {
		String contentID = null;
		if (message instanceof MimeMessage) {
			try {
				contentID = ((MimeMessage) message).getContentID();
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}

		return contentID;
	}

	@Override
	public void setFileName(String fileName) {
		try {
			message.setFileName(fileName);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getFileName() {
		String fileName = null;
		try {
			fileName = message.getFileName();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return fileName;
	}

	@Override
	public void setDisposition(ContentDisposition disposition) {
		try {
			message.setDisposition(disposition.toString());
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public ContentDisposition getDisposition() {
		String disposition = null;
		try {
			disposition = message.getDisposition();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		if (disposition != null) {
			return ContentDisposition.valueOf(disposition.toUpperCase());
		}
		return null;
	}

	@Override
	public void setHeader(String name, String value) {
		try {
			message.setHeader(name, value);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void addHeader(String name, String value) {
		try {
			message.addHeader(name, value);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void addHeaderLine(String line) {
		if (message instanceof MimeMessage) {
			try {
				((MimeMessage) message).addHeaderLine(line);
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public String[] getHeader(String name) {
		String[] headers = null;
		try {
			headers = message.getHeader(name);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return headers;
	}

	@Override
	public boolean isMimeType(String mimeType) {
		boolean isMimeType = false;
		try {
			isMimeType = message.isMimeType(mimeType);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return isMimeType;
	}

	@Override
	public String getContentType() {
		String contentType = null;
		try {
			contentType = message.getContentType();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return contentType;
	}

	@Override
	public int getSize() {
		int size = 0;
		try {
			size = message.getSize();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return size;
	}

	@Override
	public InputStream getInputStream() {
		InputStream stream = null;
		try {
			stream = message.getInputStream();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return stream;
	}

	@Override
	public void writeTo(OutputStream os) {
		try {
			message.writeTo(os);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setFrom(String fromAddrs) {
		try {
			message.setFrom(new InternetAddress(fromAddrs));
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setFrom(String fromAddrs, String fromName) {
		try {

			message.setFrom(new InternetAddress(fromAddrs, fromName, getLocalCharset()));
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getFrom() {
		String fromAddrs = null;
		try {
			Address[] addrs = message.getFrom();
			if (addrs != null && addrs.length > 0) {
				fromAddrs = addrs[0].toString();
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return fromAddrs;
	}

	@Override
	public void setReplyTo(String replyTo) {
		try {
			message.setReplyTo(InternetAddress.parse(replyTo));
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getReplyTo() {
		String replyToAddrs = null;
		try {
			Address[] addrs = message.getReplyTo();
			if (addrs != null && addrs.length > 0) {
				replyToAddrs = addrs[0].toString();
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return replyToAddrs;
	}

	@Override
	public void setRecipient(RecipientType type, List<String> addresses) {
		if(addresses == null) {
			return;
		}
		
		try {
			InternetAddress[] addressesToAdd = new InternetAddress[addresses.size()];
			int i = 0;
			for (String address : addresses) {
					InternetAddress addrs = new InternetAddress(address);
	
					final String toAddress = addrs.getAddress();
					final String toName = addrs.getPersonal();
					addressesToAdd[i++] = new InternetAddress(toAddress, toName, getLocalCharset());
			}
			message.setRecipients(getRecipientType(type), addressesToAdd);				
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public List<String> getRecipients(RecipientType type) {
		List<String> recipients = null;
		try {
			Address[] addrs = message.getRecipients(getRecipientType(type));
			if (addrs != null) {
				recipients = new ArrayList<>(addrs.length);
				for (Address address : addrs) {
					recipients.add(address.toString());
				}
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return recipients;
	}

	private Message.RecipientType getRecipientType(RecipientType type) {
		Message.RecipientType recpType = null;
		if (type == RecipientType.TO) {
			recpType = Message.RecipientType.TO;
		} else if (type == RecipientType.CC) {
			recpType = Message.RecipientType.CC;
		} else if (type == RecipientType.BCC) {
			recpType = Message.RecipientType.BCC;
		}
		return recpType;
	}

	@Override
	public List<String> getAllRecipients() {
		List<String> recipients = null;
		try {
			Address[] addrs = message.getAllRecipients();
			if (addrs != null) {
				recipients = new ArrayList<>(addrs.length);
				for (Address address : addrs) {
					recipients.add(address.toString());
				}
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return recipients;
	}

	@Override
	public String getSubject() {
		String subject = null;
		try {
			subject = message.getSubject();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return subject;
	}

	@Override
	public void setSubject(String subject) {
		try {
			if (message instanceof MimeMessage) {
				((MimeMessage) message).setSubject(subject, getLocalCharset());
			} else {
				message.setSubject(subject);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setDescription(String description) {
		try {
			if (message instanceof MimeMessage) {
				((MimeMessage) message).setDescription(description);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getDescription() {
		String description = null;
		try {
			if (message instanceof MimeMessage) {
				description = ((MimeMessage) message).getDescription();
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return description;
	}

	@Override
	public void setSentDate(Date date) {
		try {
			message.setSentDate(date);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Date getSentDate() {
		Date date = null;
		try {
			date = message.getSentDate();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return date;
	}

	@Override
	public Date getReceivedDate() {
		Date date = null;
		try {
			date = message.getReceivedDate();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return date;
	}

	@Override
	public void setAsSeen() {
		try {
			message.setFlag(Flags.Flag.SEEN, true);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean isSeen() {
		boolean isSeen = false;
		try {
			isSeen = message.isSet(Flags.Flag.SEEN);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return isSeen;
	}

	@Override
	public void setAsDeleted() {
		try {
			message.setFlag(Flags.Flag.DELETED, true);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean isDeleted() {
		boolean isDeleted = false;
		try {
			isDeleted = message.isSet(Flags.Flag.DELETED);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return isDeleted;
	}

	@Override
	public void saveChanges() {
		try {
			message.saveChanges();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public MIMEMessage getReplyMessage(boolean replyToAll) {
		MIMEMessage mimeMessage = null;
		try {
			Message msg = message.reply(replyToAll);
			mimeMessage = new JavaMailMIMEMessage(msg);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return mimeMessage;
	}

	@Override
	public void setContent(MIMEMultipart multipart) {
		Object obj = multipart.getWrappedObject();

		if (obj instanceof Multipart) {
			try {
				message.setContent((Multipart) obj);
			} catch (MessagingException ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}
	
	@Override
	public void setContent(Object obj, String type) {
		try {
			if (message instanceof MimeMessage) {
				((MimeMessage) message).setContent(obj, type);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public MIMEMultipart getContentAsMimeMultipart() {
		MIMEMultipart mimeMultipart = null;
		try {
			final Object content = message.getContent();

			if (content instanceof Multipart) {
				mimeMultipart = new JavaMailMIMEMultipart((Multipart) content);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return mimeMultipart;
	}

	@Override
	public Map<String, String> getAllHeaders(boolean applyRFC822Style) {

		Map<String, String> headersMap = new LinkedHashMap<String, String>();
		try {
			Enumeration allHdrs = null;
			if (applyRFC822Style) {
				Object content = getContentAsObject();
				if (!(content instanceof InputStream)) {
					return Collections.emptyMap();
				} else {
					InputStream is = (InputStream) content;
					InternetHeaders hdrs = new InternetHeaders(is);
					allHdrs = hdrs.getAllHeaders();
				}
			} else {
				allHdrs = ((MimeMessage) message).getAllHeaders();
			}
			while (allHdrs.hasMoreElements()) {
				Header header = (Header) allHdrs.nextElement();
				String headerName = header.getName();
				String headerValue = null;

				if (headersMap.containsKey(headerName)) {
					headerValue = headersMap.get(headerName) + ";" + header.getValue();
				} else {
					headerValue = header.getValue();
				}
				headersMap.put(headerName, headerValue);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return headersMap;
	}

	@Override
	public Enumeration getAllHeaderLines() {
		Enumeration lines = null;
		if (message instanceof MimeMessage) {
			try {
				lines = ((MimeMessage) message).getAllHeaderLines();
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}

		return lines;
	}

	@Override
	public Object getWrappedObject() {
		return message;
	}

	@Override
	public MIMEMultipart getAsSignedMultipart(List<X509Certificate> signingCerts, PrivateKey signingKey) {
		MimeMessage mimeMsg = null;
		if (message instanceof MimeMessage) {
			mimeMsg = (MimeMessage) message;
		} else {
			throw new UnsupportedOperationException();
		}

		MIMEMultipart mimeMultipart = null;

		try {
			X509Certificate signingCert = null;
			if (signingCerts != null && !signingCerts.isEmpty()) {
				signingCert = signingCerts.get(0);
			}
			final ASN1EncodableVector signedAttrs = new ASN1EncodableVector();
			final SMIMECapabilityVector caps = new SMIMECapabilityVector();
			caps.addCapability(SMIMECapability.dES_EDE3_CBC);
			caps.addCapability(SMIMECapability.rC2_CBC, 128);
			caps.addCapability(SMIMECapability.dES_CBC);
			signedAttrs.add(new SMIMECapabilitiesAttribute(caps));

			final AttributeTable signedAttributesTable = new AttributeTable(signedAttrs);
			final DefaultSignedAttributeTableGenerator signedAttributeGenerator = new DefaultSignedAttributeTableGenerator(
					signedAttributesTable);

			final SMIMESignedGenerator smGen = new SMIMESignedGenerator();
			org.bouncycastle.util.Store certStore = new JcaCertStore(signingCerts);

			final JcaSimpleSignerInfoGeneratorBuilder builder = new JcaSimpleSignerInfoGeneratorBuilder()
					.setProvider("BC");
			builder.setSignedAttributeGenerator(signedAttributeGenerator);
			final SignerInfoGenerator signerGenerator = builder.build("SHA1withRSA", signingKey, signingCert);
			smGen.addSignerInfoGenerator(signerGenerator);
			smGen.addCertificates(certStore);

			Multipart multipart = smGen.generate(mimeMsg);
			mimeMultipart = new JavaMailMIMEMultipart(multipart);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return mimeMultipart;
	}

	@Override
	public MIMEPart getAsEncryptedPart(List<X509Certificate> encCerts) {
		MimeMessage mimeMsg = null;
		if (message instanceof MimeMessage) {
			mimeMsg = (MimeMessage) message;
		} else {
			throw new UnsupportedOperationException();
		}

		MIMEPart mimePart = null;

		try {
			final SMIMEEnvelopedGenerator encrypter = new SMIMEEnvelopedGenerator();
			for (int i = 0; encCerts != null && i < encCerts.size(); i++) {
				encrypter.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(encCerts.get(i)));
			}

			final MimeBodyPart encryptedPart = encrypter.generate(mimeMsg,
					new JceCMSContentEncryptorBuilder(CMSAlgorithm.RC2_CBC).setProvider("BC").build());
			mimePart = new JavaMailPart(encryptedPart);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return mimePart;
	}

	@Override
	public InputStream getAsDecryptedStream(X509Certificate decryptCert, PrivateKey decryptKey) {
		MimeMessage mimeMsg = null;
		if (message instanceof MimeMessage) {
			mimeMsg = (MimeMessage) message;
		} else {
			throw new UnsupportedOperationException();
		}

		InputStream is = null;
		try {

			RecipientId recId = new JceKeyTransRecipientId(decryptCert);
			SMIMEEnveloped em = new SMIMEEnveloped(mimeMsg);

			RecipientInformationStore recipients = em.getRecipientInfos();
			if (recipients == null) {
				throw new EmailClientRuntimeException("No recipient information found in the encrypted message");
			}
			RecipientInformation recipient = recipients.get(recId);
			if (recipient == null) {
				throw new EmailClientRuntimeException("Listener's certficiate does not match encrypted email");
			}
			CMSTypedStream cts = recipient
					.getContentStream(new JceKeyTransEnvelopedRecipient(decryptKey).setProvider("BC"));
			if (cts == null) {
				throw new EmailClientRuntimeException("Unable to obtain contents of encrypted message");
			}

			is = cts.getContentStream();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
		return is;
	}

	@Override
	public InputStream getContentAsStream() {
		return getInputStream();
	}

	@Override
	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public String getCharset() {
		String msgCharset = null;
		try {
			String contentTypeStr = message.getContentType();
			ContentType contentType = new ContentType(contentTypeStr);
			msgCharset = contentType.getParameter("charset");
		} catch (Exception e) {
			// Do nothing, return the default charset
		}

		if (msgCharset == null || msgCharset.trim().isEmpty()) {
			msgCharset = Charset.defaultCharset().toString();
		}

		return msgCharset;
	}

	private String getLocalCharset() {
		return (charset != null ? charset : StandardCharsets.UTF_8.name());
	}

	public long getUID() 
	{
		return uid;
	}

	public void setUID(long aUID) 
	{
		uid = aUID;
	}

	public long getValidity() {
		return validity;
	}

	public void setValidity(long aValidity) {
		validity = aValidity;
	}

}
