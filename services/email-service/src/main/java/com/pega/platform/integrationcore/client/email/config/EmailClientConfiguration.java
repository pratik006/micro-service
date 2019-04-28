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

package com.pega.platform.integrationcore.client.email.config;


/**
 * EmailClientConfiguration Class encapsulating email client configuration
 * details like timeout, folderName, senderProfile, receiverProfile etc.
 * 
 * @author Unifiers
 */
public class EmailClientConfiguration {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id:$");

	private long timeout;
	private boolean disableStartTLS;
	private EmailSenderProfile senderProfile;
	private EmailReceiverProfile receiverProfile;

	/**
	 * @return the timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isDisableStartTLS() {
		return disableStartTLS;
	}

	public void setDisableStartTLS(boolean disableStartTLS) {
		this.disableStartTLS = disableStartTLS;
	}

	/**
	 * @return the senderProfile
	 */
	public EmailSenderProfile getSenderProfile() {
		return senderProfile;
	}

	/**
	 * @param senderProfile
	 *            the senderProfile to set
	 */
	public void setSenderProfile(EmailSenderProfile senderProfile) {
		this.senderProfile = senderProfile;
	}

	/**
	 * @return the receiverProfile
	 */
	public EmailReceiverProfile getReceiverProfile() {
		return receiverProfile;
	}

	/**
	 * @param receiverProfile
	 *            the receiverProfile to set
	 */
	public void setReceiverProfile(EmailReceiverProfile receiverProfile) {
		this.receiverProfile = receiverProfile;
	}

}
