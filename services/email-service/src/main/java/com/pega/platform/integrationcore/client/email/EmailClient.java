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

package com.pega.platform.integrationcore.client.email;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import com.pega.platform.integrationcore.client.ResourceClient;
import com.pega.platform.integrationcore.client.email.config.EmailClientConfiguration;

/**
 * Email client An Interface defining Email client component Provides APIs
 * required for email integration (both inbound & outbound)
 * 
 * @author Unifiers
 */
public interface EmailClient extends ResourceClient {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = "$Id:$";
	
	/**
	 * Enumeration of profile types : SENDER, RECEIVER
	 */
	public enum ProfileType {
		SENDER, RECEIVER;
	}

	/**
	 * Initializes Email client 
	 * 
	 * @param configuration
	 *            - @refer {@link EmailClientConfiguration} instance
	 * @throws EmailClientException
	 */
	public void initialize(EmailClientConfiguration configuration) throws EmailClientException;

	/**
	 * Establishes connection to email server based on initialized configuration
	 * 
	 * @param enableDebug
	 *            - to enable session debugging
	 * @param debugStream
	 *            - to provide session debug information, if it is enabled
	 * @return true if connect operation is successful
	 * @throws EmailClientException
	 */
	public boolean connect(boolean enableDebug, PrintStream debugStream) throws EmailClientException;

	/**
	 * Disconnects from email server based on initialized configuration
	 * 
	 * @return true if disconnect operation is successful, false otherwise
	 */
	public boolean disconnect();

	/**
	 * Creates a MIMEMessage instance using this email client session.
	 * 
	 * @param stream
	 *            - to initialize the content of the MIMEMessage. Specify as
	 *            <code>null</code> if no initialization is required
	 * @return MIMEMessage instance
	 * @throws EmailClientException
	 */
	public MIMEMessage createMessage(InputStream stream) throws EmailClientException;

	/**
	 * Creates a MIMEPart instance
	 * 
	 * @return MIMEPart instance
	 */
	public MIMEPart createPart();

	/**
	 * Creates a MIMEMultipart instance
	 * 
	 * @param type
	 *            - Specify the type of the multipart instance such as mixed,
	 *            alternative etc.
	 * @return MIMEMultipart instance
	 */
	public MIMEMultipart createMultipart(String type);

	/**
	 * Sends an email message
	 * 
	 * @param message
	 *            - message to send
	 * @param autoDisconnect
	 *            - specify true to disconnect the client after sending
	 * @return an instance of EmailClientResponse that contains Message-ID and
	 *         other session/connection related information
	 * @throws EmailClientException
	 */

	public EmailClientResponse sendEmail(MIMEMessage message, boolean autoDisconnect) throws EmailClientException;
	/**
	 * Retrieves emails from server
	 * 
	 * @param onlyUnread
	 *            - specify true to get only unread emails
	 * @param autoDisconnect
	 * @return list of messages retrieved
	 * @throws EmailClientException
	 */
	public List<MIMEMessage> retrieveEmails(boolean onlyUnread, boolean autoDisconnect) throws EmailClientException;

	/**
	 * Tests the connectivity
	 * 
	 * @param profileType
	 *            -specify {@link ProfileType} to test the connectivity of
	 *            either sender or receiver
	 * @return an instance of EmailClientResponse that connection related
	 *         information
	 */
	public EmailClientResponse testConnection(ProfileType profileType);
	
	/**
	 * Decodes the email text
	 * 
	 * @param etext
	 *            -encoded email text
	 * @return decoded text
	 */
	public String decodeText(String etext) throws EmailClientException;

	/**
	 * If this client uses IMAP, retrieves the message referred to by the UID if the folder's UID validity still matches. Otherwise, throws EmailClientException 
	 * @param uid - UID of the message to retrieve
	 * @param validity - UID validity value of the folder when the UID was received
	 * @return MIMEMessage of message matching UID value, or null if none found
	 */
	public MIMEMessage getMessagebyUID(long uid, long validity) throws EmailClientException;

}
