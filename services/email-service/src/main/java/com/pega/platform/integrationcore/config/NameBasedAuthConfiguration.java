/*
 * $Id: NameBasedAuthConfiguration.java 196662 2016-06-08 20:33:32Z JustinGrunau $
 *
 * Copyright (c) 2014  Pegasystems Inc.
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

package com.pega.platform.integrationcore.config;

import com.pega.pegarules.priv.LogHelper;
import com.pega.pegarules.priv.ModuleVersion;

/**
 * NameBasedAuthConfiguration
 * Configuration details that rely on a name for identification purposes
 * @version		$Revision $ $Date $
 * @author Jeff Houle
 *
 */
public abstract class NameBasedAuthConfiguration implements AuthConfiguration
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id: NameBasedAuthConfiguration.java 196662 2016-06-08 20:33:32Z JustinGrunau $");
	private static final LogHelper oLog = new LogHelper(NameBasedAuthConfiguration.class);

	/** The User Name to be used for authentication */
	protected String mUserName;
	/** The Password to be used for authentication */
	private String mPassword = null;
	/** The Host Name to be used for authentication */
	private String mHostName = "";
	/** The Domain/Realm Name to be used for authentication */
	protected String mDomain = "";
	
	/**
	 * Default constructor - takes only a name.
	 * @param aUsername - the user name for authentication
	 */
	public NameBasedAuthConfiguration(String aUsername)
	{
		mUserName = aUsername;
		mPassword = "";
	}
	
	/**
	 * Constructor - takes user name and password
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 */
	public NameBasedAuthConfiguration(String aUsername, String aPassword)
	{
		this(aUsername);
		// TODO: encryption of password ?
		mPassword = aPassword;
	}
	
	/**
	 * Constructor - takes user name, password, hostname.
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 * @param aHostName - the host name for authentication
	 */
	public NameBasedAuthConfiguration(String aUsername, String aPassword, String aHostName)
	{
		this (aUsername, aPassword);
		mHostName = aHostName;
	}
	
	/**
	 * Constructor - takes user name, password, hostname, and domain.
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 * @param aHostName - the host name for authentication
	 * @param aDomain 	- the domain/realm for authentication
	 */
	public NameBasedAuthConfiguration(String aUsername, String aPassword, String aHostName, String aDomain)
	{
		this (aUsername, aPassword);
		mHostName = aHostName;
		mDomain = aDomain;
	}
	
	/**
	 * Get the user name stored in this object
	 * @return the user name
	 */
	public String getUserName()
	{
		return mUserName;
	}
	
	/**
	 * Get the password stored in this object
	 * @return the password
	 */
	public String getPassword()
	{
		// TODO: decryption of password ?
		return mPassword;
	}
	
	/**
	 * Get the domain/realm stored in this object
	 * @return the domain/realm
	 */
	public String getDomain()
	{
		return mDomain;
	}
	
	/**
	 * Alternate call for {@link #getDomain()}
	 * @return the realm/domain
	 */
	public String getRealm()
	{
		return getDomain();
	}
	
	/**
	 * Get the Host Name stored in this object
	 * @return
	 */
	public String getHostName()
	{
		return mHostName;
	}
	
}
