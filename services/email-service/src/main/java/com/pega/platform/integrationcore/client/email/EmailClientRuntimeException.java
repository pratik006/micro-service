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
package com.pega.platform.integrationcore.client.email;

public class EmailClientRuntimeException extends RuntimeException {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	private static final long serialVersionUID = 1L;
	public static final String VERSION = "$Id:$";

	/**
	 * Exception thrown by the Email Client component whenever there
	 *  is an issue
	 */
	public EmailClientRuntimeException(String message) {
		super(message);
	}

	public EmailClientRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
