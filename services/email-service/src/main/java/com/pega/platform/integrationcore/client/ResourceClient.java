/*
 * $Id: ResourceClient.java 143278 2015-07-07 17:42:11Z JeffreyHoule $
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

package com.pega.platform.integrationcore.client;


/**
 * ResourceClient
 * An interface for an Object that acts as Pega's client 
 * when transferring content with another Resource.
 * @version		$Revision: $ $Date: $
 * @author Jeff Houle
 *
 */
public interface ResourceClient 
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id: ResourceClient.java 143278 2015-07-07 17:42:11Z JeffreyHoule $");
	
	// TODO: make generic <T extends ResourceMessage>, add a signature for "processMessage"
}
