/*
 * $Id: OAuthConfiguration.java 207235 2016-08-05 23:37:12Z CharlesReitzel $
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
 * OAuth Configuration details
 * @version		$Revision: 207235 $ $Date: 2016-08-05 19:37:12 -0400 (Fri, 05 Aug 2016) $
 * @author Jeff Houle
 *
 */
public class OAuthConfiguration implements AuthConfiguration
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id: OAuthConfiguration.java 207235 2016-08-05 23:37:12Z CharlesReitzel $");
	private static final LogHelper oLog = new LogHelper(OAuthConfiguration.class);

	public static final String SCHEME = "Bearer";
	
	/// Default parameter name to use when sending as query parameter
	public static final String PARAM_NAME = "access_token";
	
	// Default header name to use when sending as request header
	public static final String HEADER_NAME = "Authorization"; 
	
	protected String mAccessToken;
	protected final boolean mSendAsQueryParam;

	/**
	 * Initialize OAuth 2.0 Access token (nullifies empty/blank values).
	 * Sends access token as request header.
	 * @param aAccessToken OAuth 2.0 Access token
	 */
	public OAuthConfiguration(String aAccessToken) {
		this( aAccessToken, false /*by default, send as header*/ );
	}

	/**
	 * Initialize OAuth 2.0 Access token (nullifies empty/blank values).
	 * Optionally, can send token as request query parameter vs. request header.
	 * @param aAccessToken
	 * @param aSendAsQueryParam
	 */
	public OAuthConfiguration(String aAccessToken, boolean aSendAsQueryParam)
	{
		mSendAsQueryParam = aSendAsQueryParam;
		setAccessToken( aAccessToken );
	}
	
	/**
	 * @return OAuth 2.0 access token
	 */
	public String getAccessToken() {
		return mAccessToken;
	}

	/**
	 * @return Send access token as request authorization header?
	 * @return
	 */
	public boolean sendAsHeader() {
		return !mSendAsQueryParam;
	}

	/**
	 * @return Send access token as query parameter?
	 * @return
	 */
	public boolean sendAsQueryParameter() {
		return mSendAsQueryParam;
	}

	private static boolean hasValue( String aVal ) {
    	return ( null != aVal && aVal.trim().length() > 0 );
    }
	private static String hasValueOrNull( String aVal ) {
		return hasValue(aVal) ? aVal.trim() : null;
	}

	@Override
	public String getScheme() {
		return SCHEME;
	}

	public void setAccessToken(String aAccessToken) {
		mAccessToken = hasValueOrNull( aAccessToken );
	}
}
