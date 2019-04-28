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

package com.pega.platform.integrationcore.client.email.internal.javamail;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimePart;
import javax.mail.util.ByteArrayDataSource;

import com.pega.pegarules.priv.ModuleVersion;
import com.pega.platform.integrationcore.client.email.EmailClientRuntimeException;
import com.pega.platform.integrationcore.client.email.MIMEMessage;
import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;

public class JavaMailPart implements MIMEPart {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id:$");
	private Part part;

	public JavaMailPart() {
		part = new MimeBodyPart();
	}

	public JavaMailPart(Part part) {
		this.part = part;
	}

	@Override
	public void setDataHandler(byte[] data, String fileName) {
		
		if (data == null)
		{
			throw new IllegalArgumentException("Data argument not populated");
		}
		
		if (fileName == null || fileName.trim().isEmpty())
		{
			throw new IllegalArgumentException("Filename argument not populated");
		}
		
		try {
			DataSource source = new ByteArrayDataSource(data,
					FileTypeMap.getDefaultFileTypeMap().getContentType(fileName));
			part.setDataHandler(new DataHandler(source));

		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public void setDataHandler(String data, String type) {
		
		// avoid doing a trim on data
		// as that will result in data duplication
		if (data == null || data.isEmpty())
		{
			throw new IllegalArgumentException("Data argument not populated");
		}
		
		try {

			DataSource source = new ByteArrayDataSource(data, type);
			part.setDataHandler(new DataHandler(source));

		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setText(String text) {
		try {
			part.setText(text);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setText(String text, String charset, String subtype) {
		if (part instanceof MimePart) {
			try {
				((MimePart) part).setText(text, charset, subtype);
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}
	
	@Override
	public Object getContentAsObject() {
		try {
			return part.getContent();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getContentAsString() {
		String str = null;
		try {
			final Object content = part.getContent();

			if (content instanceof String) {
				str = (String) content;
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return str;
	}

	@Override
	public InputStream getContentAsStream() {
		return getInputStream();
	}

	@Override
	public MIMEMessage getContentAsMimeMessage() {
		MIMEMessage mimeMessage = null;
		try {
			final Object content = part.getContent();

			if (content instanceof Message) {
				mimeMessage = new JavaMailMIMEMessage((Message) content);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return mimeMessage;
	}

	@Override
	public void setContentID(String contentID) {
		if (part instanceof MimeBodyPart) {
			try {
				((MimeBodyPart) part).setContentID(contentID);
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public String getContentID() {
		String contentID = null;
		if (part instanceof MimeBodyPart) {
			try {
				contentID = ((MimeBodyPart) part).getContentID();
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}

		return contentID;
	}

	@Override
	public void setFileName(String fileName) {
		try {
			part.setFileName(fileName);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public String getFileName() {
		String fileName = null;
		try {
			fileName = part.getFileName();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return fileName;
	}

	@Override
	public void setDisposition(ContentDisposition disposition) {
		try {
			part.setDisposition(disposition.toString());
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public ContentDisposition getDisposition() {
		String disposition = null;
		try {
			disposition = part.getDisposition();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		if (disposition != null) {
			return ContentDisposition.valueOf(disposition.toUpperCase());
		}
		return null;
	}

	@Override
	public void setHeader(String name, String value) {
		try {
			part.setHeader(name, value);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void addHeader(String name, String value) {
		try {
			part.addHeader(name, value);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void addHeaderLine(String line) {
		if (part instanceof MimePart) {
			try {
				((MimePart) part).addHeaderLine(line);
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public String[] getHeader(String name) {
		String[] headers = null;
		try {
			headers = part.getHeader(name);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return headers;
	}

	@Override
	public boolean isMimeType(String mimeType) {
		boolean isMimeType = false;
		try {
			isMimeType = part.isMimeType(mimeType);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return isMimeType;
	}

	@Override
	public String getContentType() {
		String contentType = null;
		try {
			contentType = part.getContentType();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return contentType;
	}
	
	@Override
	public String getCharset() {
		String partCharset = null;
		try {
			String contentTypeStr = part.getContentType();
			ContentType contentType = new ContentType(contentTypeStr);
			partCharset = contentType.getParameter("charset");
		} catch (Exception e) {
			// Do nothing, return the default charset
		}

		if (partCharset == null || partCharset.trim().isEmpty()) {
			partCharset = Charset.defaultCharset().toString();
		}

		return partCharset;
	}

	@Override
	public int getSize() {
		int size = 0;
		try {
			size = part.getSize();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return size;
	}

	@Override
	public InputStream getInputStream() {
		InputStream stream = null;
		try {
			stream = part.getInputStream();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return stream;
	}

	@Override
	public void writeTo(OutputStream os) {
		try {
			part.writeTo(os);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void setContent(MIMEMultipart multipart) {
		try {
			Object obj = multipart.getWrappedObject();

			if (obj instanceof Multipart) {
				part.setContent((Multipart) obj);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}
	
	@Override
	public void setContent(Object obj, String type) {
		try {
			part.setContent(obj, type);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public Map<String, String> getAllHeaders(boolean applyRFC822Style) {
		Map<String, String> headersMap = new LinkedHashMap<String, String>();
		try {
			Enumeration allHdrs = null;
			if (applyRFC822Style) {
				Object content = getContentAsObject();
				if (!(content instanceof InputStream)) {
					return Collections.emptyMap();
				} else {
					InputStream is = (InputStream) content;
					InternetHeaders hdrs = new InternetHeaders(is);
					allHdrs = hdrs.getAllHeaders();
				}
			} else {
				allHdrs = part.getAllHeaders();
			}
			while (allHdrs.hasMoreElements()) {
				Header header = (Header) allHdrs.nextElement();
				String headerName = header.getName();
				String headerValue = null;

				if (headersMap.containsKey(headerName)) {
					headerValue = headersMap.get(headerName) + ";" + header.getValue();
				} else {
					headerValue = header.getValue();
				}
				headersMap.put(headerName, headerValue);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return headersMap;
	}

	@Override
	public Enumeration getAllHeaderLines() {
		Enumeration lines = null;
		if (part instanceof MimeBodyPart) {
			try {
				lines = ((MimeBodyPart) part).getAllHeaderLines();
			} catch (Exception ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}

		return lines;
	}

	@Override
	public Object getWrappedObject() {
		return part;
	}

	@Override
	public MIMEMultipart getContentAsMimeMultipart() {

		MIMEMultipart mimeMultipart = null;
		try {
			if (part.getContent() instanceof Multipart) {
				mimeMultipart = new JavaMailMIMEMultipart((Multipart) part.getContent());
			}
		} catch (Exception e) {
			throw new EmailClientRuntimeException(e.getMessage(), e);
		}
		return mimeMultipart;
	}
}
