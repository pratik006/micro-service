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

import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.Test;

import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;

public class TestJavaMailMIMEMultipart {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";

	MIMEMultipart mimeMultipart = null;
	MIMEPart mimePart = null;	

	@Test
	public void testCreateAddGetRemoveCountBodyPart() {
		String sampleText = "Sample text body";
		try {			
			mimeMultipart = new JavaMailMIMEMultipart("");

			Assert.assertEquals(0, mimeMultipart.getPartsCount());

			mimePart = new JavaMailPart();
			mimePart.setText(sampleText);
			mimeMultipart.addBodyPart(mimePart);

			mimePart = new JavaMailPart();
			mimePart.setDisposition(MIMEPart.ContentDisposition.ATTACHMENT);
			mimeMultipart.addBodyPart(mimePart, 1);

			Assert.assertEquals(2, mimeMultipart.getPartsCount());

			mimePart = mimeMultipart.getBodyPart(0);
			Assert.assertEquals(sampleText, mimePart.getContentAsString());

			mimePart = mimeMultipart.getBodyPart(1);
			Assert.assertEquals(MIMEPart.ContentDisposition.ATTACHMENT, mimePart.getDisposition());

			mimeMultipart.removeBodyPart(0);
			Assert.assertEquals(1, mimeMultipart.getPartsCount());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testContentType() {
		try {			
			mimeMultipart = new JavaMailMIMEMultipart("alternative");
			Assert.assertTrue("Content-Type should be multipart/alternative",
					mimeMultipart.getContentType().contains("multipart/alternative;"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWrapperObject() {
		try {
			Multipart multipart = new MimeMultipart();
			mimeMultipart = new JavaMailMIMEMultipart(multipart);
			Assert.assertTrue("Wrapped object should be same as initialized one",
					mimeMultipart.getWrappedObject() == multipart); //NOSONAR - Indeed referential equality check is needed here
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}
