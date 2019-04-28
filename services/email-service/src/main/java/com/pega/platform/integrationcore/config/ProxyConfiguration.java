/*
 * $Id: ProxyConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $
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
 * ProxyConfiguration
 * Configuration details associated with a Proxy.
 * @version		$Revision: 205409 $ $Date: 2016-07-26 21:50:37 -0400 (Tue, 26 Jul 2016) $
 * @author Jeff Houle
 *
 */
public class ProxyConfiguration extends NameBasedAuthConfiguration
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id: ProxyConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $");
	private static final LogHelper oLog = new LogHelper(ProxyConfiguration.class);

	public static final String SCHEME = "PROXY";
	
	/** The proxy's port */
	private Integer mPort;
	
	/**
	 * Constructor - takes host name, user name, password, and port.
	 * @param aHostName - the host name for authentication
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 * @param aPort 	- the host's port
	 */
	public ProxyConfiguration(String aHostName, String aUsername, String aPassword, int aPort)
	{
		this(aHostName, aUsername, aPassword);
		mPort = aPort;
	}
	
	/**
	 * Constructor - takes host name, user name, password.
	 * @param aHostName - the host name for authentication
	 * @param aUsername - the user name for authentication
 	 * @param aPassword - the password for authentication
	 */
	public ProxyConfiguration(String aHostName, String aUsername, String aPassword)
	{
		super(aUsername, aPassword, aHostName);
	}
	
	/**
	 * Constructor - takes host name
	 * @param aHostName - the host name for authentication
	 */
	public ProxyConfiguration(String aHostName)
	{
		super(null, null, aHostName);
	}
	
	/**
	 * Copy constructor
	 */
	protected ProxyConfiguration(ProxyConfiguration aBase)
	{
		this(aBase.getHostName(), aBase.getUserName(), aBase.getPassword(), aBase.getPort());
	}
	
	/**
	 * Get the port of the proxy
	 * @return the port, null if none set.
	 */
	public Integer getPort()
	{
		return mPort;
	}

	@Override
	public String getScheme() {
		return SCHEME;
	}
	
	/**
	 * Checks if this ProxyConfiguration applies to a host
	 * Classes that extend ProxyConfiguaration my use settings/options to filter hosts in/out.
	 * @param aHostname - the name of the host
	 * @return true by default.
	 */
	public boolean appliesToHost(String aHostname)
	{
		return true;
	}
}
