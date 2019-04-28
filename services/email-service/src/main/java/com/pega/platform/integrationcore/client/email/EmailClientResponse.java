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

import com.pega.pegarules.priv.ModuleVersion;

/**
 * EmailClientResponse This class contains the response parameters from email
 * client when sendEmail or testConnection apis are invoked
 * 
 * @author Unifiers
 */
public class EmailClientResponse {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id:$");

	private String messageId;
	private boolean isAuthenticationUsed;
	private boolean isSessionEstablished;
	private boolean isTransportAcquired;
	private boolean isConnected;
	private boolean isDisconnected;
	private String SMTPResponse;
	private String debugInfo;
	private boolean isStoreAcquired;
	private String errorMessage;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public boolean isAuthenticationUsed() {
		return isAuthenticationUsed;
	}

	public void setAuthenticationUsed(boolean isAuthenticationUsed) {
		this.isAuthenticationUsed = isAuthenticationUsed;
	}

	public boolean isSessionEstablished() {
		return isSessionEstablished;
	}

	public void setSessionEstablished(boolean isSessionEstablished) {
		this.isSessionEstablished = isSessionEstablished;
	}

	public boolean isTransportAcquired() {
		return isTransportAcquired;
	}

	public void setTransportAcquired(boolean isTransportAcquired) {
		this.isTransportAcquired = isTransportAcquired;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isDisconnected() {
		return isDisconnected;
	}

	public void setDisconnected(boolean isDisconnected) {
		this.isDisconnected = isDisconnected;
	}

	public String getSMTPResponse() {
		return SMTPResponse;
	}

	public void setSMTPResponse(String sMTPResponse) {
		SMTPResponse = sMTPResponse;
	}

	public String getDebugInfo() {
		return debugInfo;
	}

	public void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}
	
	public boolean isStoreAcquired() {
		return isStoreAcquired;
	}

	public void setStoreAcquired(boolean isStoreAcquired) {
		this.isStoreAcquired = isStoreAcquired;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
}
