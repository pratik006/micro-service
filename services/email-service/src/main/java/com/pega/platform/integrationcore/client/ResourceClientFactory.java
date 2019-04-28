/*
 * $Id: ResourceClientFactory.java 145130 2015-07-21 13:40:47Z JeffreyHoule $
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

import java.util.Map;

/**
 * ResourceClientFactory
 * An Interface for a Factory that produces Resource Clients
 * @version		$Revision $ $Date $
 * @author Jeff Houle
 *
 */
public interface ResourceClientFactory<T extends ResourceClient>
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id: ResourceClientFactory.java 145130 2015-07-21 13:40:47Z JeffreyHoule $");
	
	/**
	 * Get an instance of the Resource Client that this factory produces.
	 * @return a Resource Client
	 * @throws Exception
	 */
	public T getClient() throws Exception;
	
	/**
	 * Get an instance of the Resource Client that this factory produces.
	 * The map argument can be used to configure settings for this specific client.
	 * @param aOptions an optional map of settings to use for client creation
	 * @return a Resource Client
	 * @throws Exception
	 */
	public T getClient(Map aOptions) throws Exception;
	
	/**
	 * Sets an option on the factory
	 * @param aKey the name of the option
	 * @param aVal the value for the option
	 */
	public void setOption(String aKey, Object aVal);
	
	/**
	 * Resets an option to the default state (which may mean clearing it)
	 * @param aKey the name of the option
	 */
	public void resetOption(String aKey);
}
