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
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

/**
 * Interface defining methods of Message which need to be implemented by the
 * wrapper class of actual message (e.g javax.mail.Message)
 * 
 * @author Unifiers
 */
public interface MIMEMessage extends MIMEPart {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = "$Id:$";

	enum RecipientType {
		TO, CC, BCC
	}

	void setFrom(String fromAddrs);

	void setFrom(String fromAddrs, String fromName);

	String getFrom();

	void setReplyTo(String replyTo);

	String getReplyTo();

	void setRecipient(RecipientType type, List<String> addresses);

	List<String> getRecipients(RecipientType type);

	List<String> getAllRecipients();

	String getSubject();

	void setSubject(String subject);

	void setDescription(String description);

	String getDescription();

	void setSentDate(Date date);

	Date getSentDate();

	Date getReceivedDate();

	void setAsSeen();

	boolean isSeen();

	void setAsDeleted();

	boolean isDeleted();

	void saveChanges();
	
	long getUID();
	
	void setUID(long aUID);

	MIMEMessage getReplyMessage(boolean replyToAll);

	MIMEMultipart getAsSignedMultipart(List<X509Certificate> signingCerts, PrivateKey signingKey);

	MIMEPart getAsEncryptedPart(List<X509Certificate> encCerts);

	InputStream getAsDecryptedStream(X509Certificate decryptCert, PrivateKey decryptKey);

	void setCharset(String charset);

}
