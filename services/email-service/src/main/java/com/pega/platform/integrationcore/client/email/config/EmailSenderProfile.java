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
package com.pega.platform.integrationcore.client.email.config;


/**
 * EmailSenderProfile Class encapsulating receiver specific profile details
 * along with EmailProfile details
 * 
 * @author Unifiers
 */
public class EmailSenderProfile extends EmailProfile {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";

	private boolean disableAuthRetry;

	public boolean isDisableAuthRetry() {
		return disableAuthRetry;
	}

	public void setDisableAuthRetry(boolean disableAuthRetry) {
		this.disableAuthRetry = disableAuthRetry;
	}

}
