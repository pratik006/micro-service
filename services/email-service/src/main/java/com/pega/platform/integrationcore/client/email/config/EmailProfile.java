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


import java.util.Collections;
import java.util.Map;

/**
 * EmailProfile Class encapsulating email server connection details like host,
 * port, protocol, userId, password etc.
 * 
 * @author Unifiers
 */
public abstract class EmailProfile {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id:$");

	private String protocol;
	private String host;
	private int port;
	private boolean useSSL;

	// user
	private String userId;
	private String userName;
	private String userPassword;

	private Map<String, String> advancedProperties = Collections.emptyMap();

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param aProtocol
	 *            the protocol to set
	 */
	public void setProtocol(String aProtocol) {
		protocol = aProtocol;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param aHost
	 *            the host to set
	 */
	public void setHost(String aHost) {
		host = aHost;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param aUserId
	 *            the userId to set
	 */
	public void setUserId(String aUserId) {
		userId = aUserId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param aUserName
	 *            the userName to set
	 */
	public void setUserName(String aUserName) {
		userName = aUserName;
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param aUserPassword
	 *            the userPassword to set
	 */
	public void setUserPassword(String aUserPassword) {
		userPassword = aUserPassword;
	}

	/**
	 * @return the advancedProperties
	 */
	public Map<String, String> getAdvancedProperties() {
		return advancedProperties;
	}

	/**
	 * @param aAdvancedProperties
	 *            the advancedProperties to set
	 */
	public void setAdvancedProperties(Map<String, String> aAdvancedProperties) {
		advancedProperties = aAdvancedProperties;
	}

}
