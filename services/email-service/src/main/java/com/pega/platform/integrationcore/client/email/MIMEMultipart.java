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

/**
 * Interface defining methods of Multipart which need to be implemented by the
 * wrapper class of actual Multipart (e.g javax.mail.Multipart)
 * 
 * @author Unifiers
 */
public interface MIMEMultipart {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = "$Id:$";

	public enum MultipartTypes {
		MIXED("multipart/mixed"), RELATED("multipart/related"), ALTERNATE("multipart/alternate");

		private final String type;

		private MultipartTypes(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}

	void addBodyPart(MIMEPart part);

	void addBodyPart(MIMEPart part, int index);

	MIMEPart getBodyPart(int index);

	void removeBodyPart(int index);

	int getPartsCount();

	String getContentType();

	Object getWrappedObject();
}
