/*
 * $Id: BasicAuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $
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
 * BasicAuthConfiguration
 * Configuration details associated with Basic Authentication.
 * @version		$Revision: $ $Date: $
 * @author Jeff Houle
 *
 */
public class BasicAuthConfiguration extends NameBasedAuthConfiguration
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id: BasicAuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $");
	private static final LogHelper oLog = new LogHelper(BasicAuthConfiguration.class);
	
	/** The state of preemptive authentication */
	private boolean mPreemptive = false;
	
	public static final String SCHEME = "Basic";
	
	/**
	 * Default constructor - takes only a name.
	 * @param aUsername - the user name for authentication
	 */
	public BasicAuthConfiguration(String aUsername)
	{
		super(aUsername);
	}
	
	/**
	 * Constructor - takes user name and password
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 */
	public BasicAuthConfiguration(String aUsername, String aPassword)
	{
		super(aUsername, aPassword);
	}
	
	/**
	 * Constructor - takes user name and password
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 * @param aPre - whether to use preemptive authentication (pass the header in first request rather than waiting for 401 to re-send with header)
	 */
	public BasicAuthConfiguration(String aUsername, String aPassword, boolean aPre )
	{
		super(aUsername, aPassword);
		mPreemptive = aPre ;
	}
	
	/**
	 * Constructor - takes user name, password, hostname, and domain.
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 * @param aHostName - the host name for authentication
	 * @param aDomain 	- the domain/realm for authentication
	 */
	public BasicAuthConfiguration(String aUsername, String aPassword, String aHostName, String aDomain)
	{
		super(aUsername, aPassword, aHostName, aDomain);
	}
	
	/**
	 * Constructor - takes user name, password, hostname, and domain.
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 * @param aHostName - the host name for authentication
	 * @param aDomain 	- the domain/realm for authentication
	 * @param aPre - whether to use preemptive authentication (pass the header in first request rather than waiting for 401 to re-send with header)
	 */
	public BasicAuthConfiguration(String aUsername, String aPassword, String aHostName, String aDomain, boolean aPre)
	{
		super(aUsername, aPassword, aHostName, aDomain);
		mPreemptive = aPre ;
	}	
	
	/**
	 * Set the state of preemptive authentication,
	 * (sending credentials on initial request)
	 * @param aPre - the desired setting for preemptive auth
	 */
	public void setPreemptive(boolean aPre)
	{
		mPreemptive = aPre;
	}
	
	/**
	 * Get the state of preemptive authentication,
	 * (sending credentials on initial request)
	 * @return the preemptive auth setting
	 */
	public boolean getPreemptive()
	{
		return mPreemptive;
	}

	@Override
	public String getScheme() {
		return SCHEME;
	}
}
