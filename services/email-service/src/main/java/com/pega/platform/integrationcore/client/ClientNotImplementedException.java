/*
 * $Id: ClientNotImplementedException.java 207693 2016-08-11 23:17:59Z DavisWalsh $
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

package com.pega.platform.integrationcore.client;

/**
 * Exception thrown when a client type is not implemented
 *
 */
public class ClientNotImplementedException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public static final String COPYRIGHT = "Copyright (c) 2015  Pegasystems Inc.";
	//	public static final String VERSION = "$Id: ClientNotImplementedException.java 207693 2016-08-11 23:17:59Z DavisWalsh $";
	//public static final String VERSION = ModuleVersion.register("$Id: ClientNotImplementedException.java 207693 2016-08-11 23:17:59Z DavisWalsh $");
	//private static final LogHelper oLog = new LogHelper(
			//ClientNotImplementedException.class);

	public ClientNotImplementedException() 
	{
		super("This type of client is not available from this Factory.");
	}

	public ClientNotImplementedException(String aMessage) 
	{
		super(aMessage);
	}

}
