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
 * SystemDefaultHTTPProxyConfiguration
 * 
 * A singleton ProxyConfiguration that reflects the JVM args http.ProxyHost, etc.
 * <br/> The {@link #appliesToHost(String)} method of this implementation reflects the http.nonProxyHosts JVM arg.
 *
 * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/net/doc-files/net-properties.html?cm_mc_uid=97448896458115003168204&cm_mc_sid_50200000=1501016006">Oracle doc</a>
 * 
 * @version		$Revision$ $Date$
 * @author Jeff Houle
 *
 */
public class SystemDefaultProxyConfigurationHTTP extends ProxyConfiguration {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//	public static final String VERSION = "$Id:$";
	public static final String VERSION = ModuleVersion.register("$Id:$");
	private static final LogHelper oLog = new LogHelper(SystemDefaultProxyConfigurationHTTP.class);
	
	// constants for System arguments
	protected static final String SUFFIX_HOST = ".proxyHost";
	protected static final String SUFFIX_USER = ".proxyUser";
	protected static final String SUFFIX_PW = ".proxyPassword";
	protected static final String SUFFIX_PORT = ".proxyPort";
	protected static final String NON_PROXY_HOSTS = "http.nonProxyHosts";
	
	 /** HostnameMatcher for checking hostnames against nonProxyHosts */
	private static HostnameMatcher NON_PROXY_MATCHER = null;
	
	 /** Flag for knowing if we attempted to init {@link #NON_PROXY_MATCHER} as null is an acceptable state for it. */
    private static boolean NON_PROXY_INIT_FLAG = false;
   
    /** Singleton member. All users of SystemDefaultProxyConfigurationHTTP are expected to share this. */
	private static SystemDefaultProxyConfigurationHTTP mInstance;

	static
	{
		init();
	}
	
	/**
	 * (Re)initialize this proxy configuration based on current system settings
	 */
	protected static void init()
	{
		ProxyConfiguration baseConfig = getBaseConfig("http");
		if (null == baseConfig)
		{
			mInstance = null;
		}
		else
		{
			mInstance = new SystemDefaultProxyConfigurationHTTP(baseConfig);
		}
		
		// force (re)read of nonproxy hosts if we're (re)initializing this object
		NON_PROXY_INIT_FLAG = false;
	}
	
	/**
	 * Copy Constructor - Inherit the properties of a generic Proxy Configuration
	 * Used internally and by child classes.
	 * @param aBase another ProxyConfiguration object
	 */
	protected SystemDefaultProxyConfigurationHTTP(ProxyConfiguration aBase)
	{
		super(aBase);
	}

	/**
	 * Get the system-wide instance of this class 
	 * @return the system-wide Proxy Configuration for HTTP, null if none.
	 */
	public static SystemDefaultProxyConfigurationHTTP getInstance()
	{
		return mInstance;
	}
	
	@Override
	public boolean appliesToHost(final String aHostname) 
	{
		if (null == aHostname)
		{
			// an invalid case -- we'll say it applies.
			return true;
		}

		final HostnameMatcher pool = getNonProxyHostsPool();
		if (null == pool || pool.matchesAll())
		{
			// no information about non-proxy hosts, so this proxy configuration applies to all hosts
			return true;
		}

		// synchronized because the "match" method on HostnameMatcher has side-effects that include modifying Maps/Collections.
		// If thread A is updating the information while thread B is attempting to read it, there would be Exceptions.
		//
		// Unfortunately, this is a potential bottleneck, as one thread could be waiting for another thread to finish checking!
		// As of July 2017 (7.3.1) we are doing this in order to make the match method have a best runtime of O(1) by caching
		// rather than the O(n) or worse time of running regular expressions every time we call "match."
		//
		// This is NEVER called on systems that do not have the "http.nonProxyHosts" JVM setting.
		//
		synchronized ( SystemDefaultProxyConfigurationHTTP.class ) 
		{
			return ! pool.match(aHostname);
		}
	}

	/**
	 * Use the {@value #NON_PROXY_HOSTS} system property to initialize a HostnameMatcher.
	 * This is only to be called as a helper by {@link #getNonProxyHostsPool()}
	 * @return the HostnameMatcher for nonProxyHosts, null if no usable logic found.
	 */
	private static HostnameMatcher initNonProxyHosts() 
	{
		HostnameMatcher matcher = null;
    	String nphosts = System.getProperty( NON_PROXY_HOSTS );
    	if (null == nphosts || nphosts.trim().length() == 0)
    	{
    		return null;
    	}
   	
    	matcher = new HostnameMatcher(nphosts);
    	
    	// if the value did not result in any logic, the matcher may be devoid of filtering logic.
    	if (! matcher.matchesAll())
    	{
    		return matcher;
    	}
    	
    	return null;
	}

	/**
	 * Get the HostnameMatcher currently used for nonProxyHosts.
	 * Initialize first if that hasn't been attempted yet.
	 * 
	 * @return the HostnameMatcher representing nonProxyHosts, null if nonProxyHosts did not contain a useful/usable value.
	 */
    protected static HostnameMatcher getNonProxyHostsPool() 
    {
    	if ( !NON_PROXY_INIT_FLAG ) {
    		synchronized ( SystemDefaultProxyConfigurationHTTP.class ) {
    	    	if ( !NON_PROXY_INIT_FLAG ) {
    	    		// only a bottleneck on the first time.
			        NON_PROXY_MATCHER = initNonProxyHosts();
			    	NON_PROXY_INIT_FLAG = true;
		    	}
    		}
    	}
        return NON_PROXY_MATCHER;
    }
    
    /**
     * Get a proxy configuration that reflects the system settings for a certain protocol.
     * The result is copied (via copy constructor) to create the full-featured Proxy Configuration.
     * @param aProtocol the protocol (http
     * @return the basic ProxyConfiguration that represents the system properties for proxy (other than nonProxyHosts)
     */
    protected static ProxyConfiguration getBaseConfig(String aProtocol)
    {
    	String proxyHost = "";
		String proxyUser = "";
		String proxyPassword = "";
		int proxyPort = -1;

		String protocol = aProtocol;
		if (protocol == null)
		{
			return null;
		}
		
		// settings always start with lowercase name of protocol
		protocol = protocol.trim().toLowerCase();
		if (protocol.length() == 0)
		{
			return null;
		}

		try
		{
			proxyHost = System.getProperty(protocol + SUFFIX_HOST);
			
			if ( proxyHost == null || proxyHost.trim().length() == 0) {
				oLog.debug(protocol + " proxy host setting not found. Skipping setup.");
				return null;
			}

			String strProxyPort = System.getProperty(protocol + SUFFIX_PORT);				
			proxyPort = Integer.parseInt(strProxyPort);
			proxyUser = System.getProperty(protocol + SUFFIX_USER);
			proxyPassword = System.getProperty(protocol + SUFFIX_PW);
			
		}
		catch (Exception e)
		{
			oLog.error("Unable to parse proxy settings for " + protocol + ". Skipping setup.", e);
			return null;
		}

		return new ProxyConfiguration(proxyHost, proxyUser, proxyPassword, proxyPort);
    }
}
