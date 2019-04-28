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
 * EmailReceiverProfile Class encapsulating receiver specific profile details
 * along with EmailProfile details
 * 
 * @author Unifiers
 */
public class EmailReceiverProfile extends EmailProfile {

	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//public static final String VERSION = ModuleVersion.register("$Id:$");

	private String folderName;
	private long fetchSize;
	private boolean isPartialFetch;

	/**
	 * @return the fetchSize
	 */
	public long getFetchSize() {
		return fetchSize;
	}

	/**
	 * @param fetchSize
	 *            the fetchSize to set
	 */
	public void setFetchSize(long fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * @return the isPartialFetch
	 */
	public boolean getIsPartialFetch() {
		return isPartialFetch;
	}

	/**
	 * @param isPartialFetch
	 *            the partialFetch to set
	 */
	public void setIsPartialFetch(boolean isPartialFetch) {
		this.isPartialFetch = isPartialFetch;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

}
