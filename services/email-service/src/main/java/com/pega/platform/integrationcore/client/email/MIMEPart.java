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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Map;

/**
 * Interface defining methods of Part which need to be implemented by the
 * wrapper class of actual Part (e.g javax.mail.Part)
 * 
 * @author Unifiers
 */
public interface MIMEPart {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = "$Id:$";

	public enum ContentDisposition {
		ATTACHMENT("attachment"), INLINE("inline");

		private final String type;

		private ContentDisposition(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
		
		public static final ContentDisposition getContentDisposition(String type) {
			for (ContentDisposition value : ContentDisposition.values()) {
				if(value.getType().equalsIgnoreCase(type)) {
					return value;
				}
			}
			return ContentDisposition.ATTACHMENT;
		}
	}
	
	void setContent(Object obj, String type);

	void setContent(MIMEMultipart multipart);

	void setDataHandler(byte[] data, String fileName);
	
	void setDataHandler(String data, String type);

	void setText(String text);

	void setText(String text, String charset, String subtype);
	
	Object getContentAsObject();

	String getContentAsString();

	InputStream getContentAsStream();

	MIMEMessage getContentAsMimeMessage();

	MIMEMultipart getContentAsMimeMultipart();

	void setContentID(String contentId);

	String getContentID();

	void setFileName(String fileName);

	String getFileName();

	void setDisposition(ContentDisposition disposition);

	ContentDisposition getDisposition();

	void setHeader(String name, String value);

	void addHeader(String name, String value);

	void addHeaderLine(String line);

	String[] getHeader(String name);

	Map<String, String> getAllHeaders(boolean applyRFC822Style);

	Enumeration getAllHeaderLines();

	boolean isMimeType(String mimetype);

	String getContentType();
	
	String getCharset();

	int getSize();

	InputStream getInputStream();

	void writeTo(OutputStream outputStream);

	Object getWrappedObject();
	
}
