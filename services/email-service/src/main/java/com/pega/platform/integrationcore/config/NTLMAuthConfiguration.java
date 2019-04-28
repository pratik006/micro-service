/*
 * $Id: NTLMAuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $
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
 * Configuration details associated with NTLM Authentication.
 * NTLMAuthConfiguration
 * @version		$Revision$ $Date$
 * @author Jeff Houle
 *
 */
public class NTLMAuthConfiguration extends NameBasedAuthConfiguration 
{

	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id: NTLMAuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $");
	private static final LogHelper oLog = new LogHelper(NTLMAuthConfiguration.class);

	public static final String SCHEME = "NTLM";
	private boolean enableJCIFs = true;
	

	/**
	 * Default constructor - takes only a name.
	 * @param aUsername - the user name for authentication of the form domain\\username
	 */
	public NTLMAuthConfiguration(String aUsername)
	{
		super(aUsername);
	}
	
	/**
	 * Constructor - takes user name and password
	 * @param aUsername - the user name for authentication of the form domain\\username
 	 * @param aPassword - the password for authentication
	 */
	public NTLMAuthConfiguration(String aUsername, String aPassword)
	{
		super(aUsername, aPassword);
	}

	/**
	 * Constructor.
	 * @param aUsername the user name for the NTLM credentials
	 * @param aPassword the password for the NTLM credentials
	 * @param aHostName the host name for the NTLM credentials
	 * @param aDomain the realm for the NTLM credentials
	 */
	public NTLMAuthConfiguration(String aUsername, String aPassword,
			String aHostName, String aDomain) 
	{
		this(aUsername, aPassword, aHostName, aDomain, true);
	}
	
	/**
	 * Constructor.
	 * @param aUsername the user name for the NTLM credentials
	 * @param aPassword the password for the NTLM credentials
	 * @param aHostName the host name for the NTLM credentials
	 * @param aDomain the realm for the NTLM credentials
	 */
	public NTLMAuthConfiguration(String aUsername, String aPassword,
			String aHostName, String aDomain, boolean enableJCIFs) 
	{
		super(aUsername, aPassword, aHostName, aDomain);
		this.enableJCIFs = enableJCIFs;
		
		 // interpreting domain from user name
		interpretDomain(System.getProperty("http.auth.ntlm.domain"));		
	}
	
	/**
	 * When domain is empty, attempt scraping it from username.
	 * This is for NTLM-style authentication where user may have supplied a DOMAIN\USERNAME but no explicit domain.
	 * @param aDefault the default Domain to use, null if none.
	 */
	protected void interpretDomain(final String aDefault)
	{
		if (mDomain != null && mDomain.trim().length() > 0)
		{
			// already have a domain
			return;
		}
		
		mDomain = aDefault;
		
		// check username for realm
		String user = mUserName;

		if (user == null || user.trim().length() == 0)
		{
			return;
		}

		int loc = user.lastIndexOf('\\');

		if (loc > 0 && loc < user.length() - 1)
		{
			final String domain = user.substring(0, loc);
			if (mDomain == null || mDomain.length() == 0 || mDomain.equalsIgnoreCase(domain))
			{
				mDomain = domain;
				mUserName = user.substring(loc + 1);
				return;
			}
		}
	}
	
	@Override
	public String getScheme() {
		return SCHEME;
	}
	
	public boolean enableJCIFs() {
		return enableJCIFs;
	}
}
