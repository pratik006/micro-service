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

package com.pega.platform.integrationcore.config;

import com.pega.pegarules.priv.LogHelper;
import com.pega.pegarules.priv.ModuleVersion;

/**
 * SystemDefaultHTTPSProxyConfiguration
 * 
 * A singleton ProxyConfiguration that reflects the JVM args https.ProxyHost, etc.
 * <br/>
 * Note on nonProxyHosts: <b>http.</b>nonProxyHosts applies to both HTTP <b>and</b> HTTPS.
 * 
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html?cm_mc_uid=97448896458115003168204&cm_mc_sid_50200000=1501016006">Oracle doc</a>
 * @version		$Revision$ $Date$
 * @author Jeff Houle
 *
 */
public class SystemDefaultProxyConfigurationHTTPS extends SystemDefaultProxyConfigurationHTTP {

	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//	public static final String VERSION = "$Id:$";
	public static final String VERSION = ModuleVersion.register("$Id:$");
	private static final LogHelper oLog = new LogHelper(SystemDefaultProxyConfigurationHTTPS.class);
	
	private static SystemDefaultProxyConfigurationHTTPS mInstance;

	static
	{
		init();
	}
	
	/**
	 * (Re)initialize this proxy configuration based on current system settings
	 */
	public static void init()
	{
		// Use init code from parent class SystemDefaultHTTPProxyConfiguration, but use https as protocol
		ProxyConfiguration baseConfig = getBaseConfig("https");
		if (null == baseConfig)
		{
			mInstance = null;
		}
		else
		{
			mInstance = new SystemDefaultProxyConfigurationHTTPS(baseConfig);
		}
	}
	
	/**
	 * Copy constructor
	 * @param aBase a ProxyConfiguration to copy
	 */
	private SystemDefaultProxyConfigurationHTTPS(ProxyConfiguration aBase) 
	{
		super(aBase);
	}

	/**
	 * Get the system-wide instance of this type
	 * @return the system-wide instance of this type, null if system settings did not define anything for HTTPS.
	 */
	public static SystemDefaultProxyConfigurationHTTPS getInstance()
	{
		return mInstance;
	}
}
