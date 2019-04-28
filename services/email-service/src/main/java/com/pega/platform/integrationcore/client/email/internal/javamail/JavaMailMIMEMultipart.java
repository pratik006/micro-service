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

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

import com.pega.pegarules.priv.ModuleVersion;
import com.pega.platform.integrationcore.client.email.EmailClientRuntimeException;
import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;
import com.pega.platform.integrationcore.internal.IntegrationUtils;

public class JavaMailMIMEMultipart implements MIMEMultipart {

	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	public static final String VERSION = ModuleVersion.register("$Id:$");

	private Multipart multipart;

	public JavaMailMIMEMultipart(String type) {
		if (IntegrationUtils.hasValue(type)) {
			multipart = new MimeMultipart(type);
		} else {
			multipart = new MimeMultipart();
		}
	}

	public JavaMailMIMEMultipart(Multipart multipart) {
		this.multipart = multipart;
	}

	@Override
	public void addBodyPart(MIMEPart part) {
		try {
			Object obj = part.getWrappedObject();

			if (obj instanceof BodyPart) {
				multipart.addBodyPart((BodyPart) obj);
			}
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public void addBodyPart(MIMEPart part, int index) {
		Object obj = part.getWrappedObject();

		if (obj instanceof BodyPart) {
			try {
				multipart.addBodyPart((BodyPart) obj, index);
			} catch (MessagingException ex) {
				throw new EmailClientRuntimeException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public MIMEPart getBodyPart(int index) {
		MIMEPart mimePart = null;
		try {
			final BodyPart part = multipart.getBodyPart(index);
			mimePart = new JavaMailPart(part);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return mimePart;
	}

	@Override
	public void removeBodyPart(int index) {
		try {
			multipart.removeBodyPart(index);
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}
	}

	@Override
	public int getPartsCount() {
		int count = 0;
		try {
			count = multipart.getCount();
		} catch (Exception ex) {
			throw new EmailClientRuntimeException(ex.getMessage(), ex);
		}

		return count;
	}

	@Override
	public String getContentType() {
		return multipart.getContentType();
	}

	@Override
	public Object getWrappedObject() {
		return multipart;
	}

}
