/*
 * $Id: IntegrationUtils.java 204564 2016-07-21 15:14:47Z CharlesReitzel $
 *
 * Copyright (c) 2015  Pegasystems Inc.
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

package com.pega.platform.integrationcore.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Common noise routines.
 * 
 * IntegrationUtils
 * @version		$Revision: 204564 $ $Date: 2016-07-21 11:14:47 -0400 (Thu, 21 Jul 2016) $
 * @author reitc
 */
public class IntegrationUtils 
{
	public static final String COPYRIGHT = "Copyright (c) 2015  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id: IntegrationUtils.java 204564 2016-07-21 15:14:47Z CharlesReitzel $");
	//protected static final LogHelper oLog = new LogHelper( IntegrationUtils.class );
	protected static final Logger oLog = LoggerFactory.getLogger(IntegrationUtils.class);

	public static boolean hasValue( String aVal ) {
    	return ( null != aVal && aVal.trim().length() > 0 );
    }
    public static boolean isBlank( String aVal ) {
    	return !hasValue( aVal );
    }
    public static String hasValueOrNull( String aVal ) {
    	return ( hasValue(aVal) ? aVal.trim() : null );
    }
    public static int safeSize( String aVal ) {
    	return ( hasValue(aVal) ? aVal.trim().length() : 0 );
    }
    public static String safeToString( Object aVal ) {
    	return ( null != aVal ? aVal.toString() : null );
    }

    public static boolean hasData( Collection<?> aColl ) {
    	return ( null != aColl && aColl.size() > 0 );
    }
    public static boolean hasData( Map<?,?> aColl ) {
    	return ( null != aColl && aColl.size() > 0 );
    }  
    public static <T> boolean hasData( T[] aArr ) {
    	return ( null != aArr && aArr.length > 0 );
    }
    

    public static int safeSize( Collection<?> aColl ) {
    	return ( null != aColl ? aColl.size() : 0 );
    }
    public static int safeSize( Map<?,?> aColl ) {
    	return ( null != aColl ? aColl.size() : 0 );
    }  
    public static <T> int safeSize( T[] aArr ) {
    	return ( null != aArr ? aArr.length : 0 );
    }
    
    public static <T> T nullValue( T aObj, T aDflt ) {
    	return ( null != aObj ? aObj : aDflt );
    }
    
	public static int stringsCompare( String aLeft, String aRight ) 
	{
		if (aLeft == aRight) {
			// both null or same pointer
			return 0;
		}
		if (aLeft == null) {
			return -1;
		}
		if (aRight == null) {
			return 1;
		}
		return aLeft.compareTo( aRight );
	}

	public static int stringsCompareNoCase( String aLeft, String aRight ) 
	{
		if (aLeft == aRight) {
			// both null or same pointer
			return 0;
		}
		if (aLeft == null) {
			return -1;
		}
		if (aRight == null) {
			return 1;
		}
		return aLeft.compareToIgnoreCase( aRight );
	}
	
	public static boolean stringsEqual( String aLeft, String aRight ) {
		return ( stringsCompare(aLeft, aRight) == 0 );
	}
	public static boolean stringsEqualNoCase( String aLeft, String aRight ) {
		return ( stringsCompareNoCase(aLeft, aRight) == 0 );
	}
	
	public static String getSystemProperty( String propName ) {
		return getSystemProperty( propName, null );
	}
	
	public static String getSystemProperty( String propName, String defaultValue ) 
	{
		String propValue = null, reason = null;
		try  {
			if ( hasValue(propName) ) {
				propValue = System.getProperty( propName );
			}
		} 
		catch (SecurityException secEx) {
			reason = "no access system properties";
		}
		if  ( isBlank(propValue) ) {
			if ( oLog.isDebugEnabled() && hasValue(defaultValue) ) {
				reason = nullValue( reason, "property not set" );
				oLog.debug( "Using default value '" + defaultValue + "' of property '" + propName + "': " + reason );
			}
			propValue = defaultValue;
		}
		return propValue;
	}
	
	public static boolean getSystemProperty( String aPropName, boolean aDefaultValue ) 
	{
		String propValue = getSystemProperty( aPropName );
		if ( hasValue(propValue) ) {
			return Boolean.parseBoolean( propValue );
		}
		return aDefaultValue;
	}
	
	public static boolean isValuePresent( String[] aValues, String aValue )
	{
		if ( hasData(aValues) && hasValue(aValue) ) {
			for ( String value : aValues ) {
				if ( aValue.equals(value) ) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isValuePresent( String[] aValues, String aValue, boolean aDisableCaseSensitivity )
	{
		if ( ! hasData(aValues) || ! hasValue(aValue) ) 
		{
			return false;
		}
		
		for ( String value : aValues ) 
		{
			if (((aDisableCaseSensitivity && aValue.equalsIgnoreCase(value)) || aValue.equals(value)))
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Force use of UTF-8 for URL encoding.  It's mandated by RFC.
	 * UTF-8 is baked into the JRE, so encoding must be supported.
	 * @param aUrlPart
	 * @return URL encoded form of aUrlPart
	 */
	public static String urlEncode( String aUrlPart )
	{
		try {
			aUrlPart = URLEncoder.encode(aUrlPart, "UTF-8");
		} 
		catch (UnsupportedEncodingException ex) {
			// Literally cannot happen for UTF-8 and Java
		}
		return aUrlPart;
	}

	/**
	 * Force use of UTF-8 for URL decoding.  It's mandated by RFC.
	 * UTF-8 is baked into the JRE, so encoding must be supported.
	 * @param aUrlPart
	 * @return URL decoded form of previously encoded aUrlPart
	 */
	public static String urlDecode( String aUrlPart )
	{
		try {
			aUrlPart = URLDecoder.decode(aUrlPart, "UTF-8");
		} 
		catch (UnsupportedEncodingException ex) {
			// Literally cannot happen for UTF-8 and Java
		}
		return aUrlPart;
	}

	public static String getBaseFileName( String aFilePath )
	{
		aFilePath = aFilePath.trim();
		int ixLastSlash = aFilePath.lastIndexOf( '/' );
		int ixLastBackSlash = aFilePath.lastIndexOf( '\\' );
		int ixLastPath = Math.max(ixLastSlash, ixLastBackSlash);
		if ( ixLastPath >= 0 ) {
			aFilePath = aFilePath.substring( ixLastPath+1 );
		}
		return aFilePath;
	}

	/**
	 * Returns portion of file name to the right of the last '.' 
	 * character, excluding the '.' character itself.
	 * 
	 * @param aFileName
	 * @return File extension or null, if no '.' character.
	 */
	public static String getFileExtension( String aFileName )
	{
		String ext = null;
		aFileName = getBaseFileName( aFileName );
		int ixLastDot = aFileName.lastIndexOf('.');
		if ( ixLastDot > 0 ) {
			ext = aFileName.substring( ixLastDot+1 );
		}
		return ext;
	}
	
	/**
	 * Unique alphanumeric string of length 32.
	 * @return String
	 */
	public  static String getServiceInvocationId() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/**
	 * Safely unboxes a Boolean
	 * @param aBool a Booleant o check
	 * @return true if the Boolean is not null and equal to true, false otherwise
	 */
	public static boolean safeUnbox(Boolean aBool)
	{
		return (aBool != null && aBool.booleanValue());
	}
	
	/**
	 * Checks if a byte array exists and has elements
	 * @param aBytes a byte array
	 * @return true if the byte array exists and can be consumed
	 */
	public static boolean hasBytes(byte[] aBytes)
	{
		return (null != aBytes && aBytes.length > 0);
	}
}
