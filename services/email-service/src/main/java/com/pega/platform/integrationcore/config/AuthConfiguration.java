/*
 * $Id: AuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $
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

import com.pega.pegarules.priv.ModuleVersion;
/**
 * AuthConfiguration
 * Authentication details which may be required for successful transmission or reception
 * of information transferred between Pega and another resource.
 * @version		$Revision: $ $Date: $
 * @author Jeff Houle
 *
 */
public interface AuthConfiguration
{
	public static final String COPYRIGHT = "Copyright (c) 2014  Pegasystems Inc.";
	//	public static final String VERSION = "$Id: AuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $";
	public static final String VERSION = ModuleVersion.register("$Id: AuthConfiguration.java 205409 2016-07-27 01:50:37Z CharlesReitzel $");
	
	String getScheme();
}
