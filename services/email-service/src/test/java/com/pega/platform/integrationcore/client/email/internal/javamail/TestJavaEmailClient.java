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

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.Test;

import com.pega.platform.integrationcore.client.email.EmailClient;
import com.pega.platform.integrationcore.client.email.EmailClient.ProfileType;
import com.pega.platform.integrationcore.client.email.EmailClientException;
import com.pega.platform.integrationcore.client.email.EmailClientResponse;
import com.pega.platform.integrationcore.client.email.MIMEMessage;
import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;
import com.pega.platform.integrationcore.client.email.config.EmailClientConfiguration;
import com.pega.platform.integrationcore.client.email.config.EmailSenderProfile;
import com.pega.platform.integrationcore.client.email.internal.javamail.stubs.TransportStub;
import com.sun.mail.util.QEncoderStream; //NOSONAR used to hide real quality flaw
import org.springframework.util.Base64Utils;

public class TestJavaEmailClient {
	public static final String COPYRIGHT = "Copyright (c) 2017  Pegasystems Inc.";
	//	public static final String VERSION = "$Id:$";
	SampleEmailClientConfiguration clientconfig=new SampleEmailClientConfiguration();
	// Before setting Email configuration
	@Test (expected = EmailClientException.class)
	public void testConnectException() throws EmailClientException {
		EmailClient client = new JavaEmailClient();
		client.connect(false, null);
	}
	
	@Test
	public void testConnect() throws EmailClientException {
		EmailClient client = new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		Assert.assertTrue("Connection should be established...", client.connect(false, null));
	}
	
	@Test (expected = EmailClientException.class)
	public void testSendEmailException() throws EmailClientException {
		EmailClient client = new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		client.connect(false, null);
		client.sendEmail(null, true);
	}
	
	@Test
	public void test1_sendEmail() throws EmailClientException {
		EmailClient client = null;
		EmailClientResponse response = null;
		client = new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		client.connect(false, null);

		MIMEMessage message=clientconfig.prepareMessage(client,"Sample simple email subject!");
		message.setText("Sample simple email TEXT!");
		response = client.sendEmail(message, true);
		Assert.assertTrue("Authentication should have used", response.isAuthenticationUsed());
		clientconfig.verifyResponse(response);
	}
	
	@Test
	public void test2_retrieveEmails() {
		clientconfig.verifyMessagesArrival();
		MIMEMessage message = clientconfig.messages.get(0);
		clientconfig.verifySimpleEmailProperties(message);		
		message.setAsSeen();
		clientconfig.client.disconnect();
	}
	
	@Test
	public void testSenderAuthRetry() throws EmailClientException {
		try {
			EmailClientConfiguration config = clientconfig.getClientConfiguration();
			EmailSenderProfile sender = config.getSenderProfile();
			sender.setDisableAuthRetry(true);
			sender.setPort(25);

			TransportStub.enableAuthRetry(true);

			EmailClient client = new JavaEmailClient();
			client.initialize(config);
			boolean connected = client.connect(false, null);
			Assert.assertTrue("Email client connection should be successful", connected);
			
		} finally {
			TransportStub.enableAuthRetry(false);
		}
	}

	@Test
	public void testReceiveEmailException() throws EmailClientException {
		EmailClient client = new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		List<MIMEMessage> messages = client.retrieveEmails(true, true);
		Assert.assertNotNull("Messages should not be null", messages);
	}
	
	@Test
	public void testCreateMimeMessage() throws EmailClientException {
		EmailClient client = new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		client.connect(false, null);
		MIMEMessage message = client.createMessage(null);
		message.setText("Sample email body to test CreateMimeMessage");
		Assert.assertEquals("Sample email body to test CreateMimeMessage", message.getContentAsString());
	}
	
	@Test
	public void testCreateMimePart() {
		EmailClient client = new JavaEmailClient();
		MIMEPart mimePart = client.createPart();
		mimePart.setText("Created Mimepart");
		mimePart.setHeader("Content-type", "text/html");
		System.out.println(mimePart);
		Assert.assertEquals("text/html", mimePart.getContentType());
		Assert.assertEquals("Created Mimepart", mimePart.getContentAsString());
	}
	
	@Test
	public void testCreateMimeMultiPart() {
		EmailClient client = new JavaEmailClient();
		MIMEMultipart mimeMultiPart = client.createMultipart(null);
		Assert.assertTrue(mimeMultiPart instanceof MIMEMultipart);
		Assert.assertTrue(mimeMultiPart.getWrappedObject() instanceof MimeMultipart);
	}
	
	@Test
	public void testConnectionResponse() throws EmailClientException {
		EmailClient client =  new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		client.connect(false, null);
		EmailClientResponse connResponse = client.testConnection(ProfileType.SENDER);
		Assert.assertTrue("Sender should be connected", connResponse.isConnected());
		Assert.assertTrue("Session should be established", connResponse.isSessionEstablished());
		Assert.assertTrue("Sender should be disconnected", connResponse.isDisconnected());
		Assert.assertTrue("Transport should be Acquired", connResponse.isTransportAcquired());
	}
	
	@Test
	public void testDisconnect() throws EmailClientException {
		EmailClient client = new JavaEmailClient();
		client.initialize(clientconfig.getClientConfiguration());
		client.connect(false, null);
		Assert.assertTrue("Connection should be disconneted", client.disconnect());
	}

	@Test
	public void testDecodeText() {
		try {
			EmailClient client = new JavaEmailClient();
			String text = "Sample text to test EmailClient.decodeText()";
			String str = "=?UTF-8?B?" + Base64Utils.encodeToString(text.getBytes()) + "?=";
			Assert.assertEquals(text, client.decodeText(str));

			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			QEncoderStream qEncoderStream = new QEncoderStream(outStream, true);
			byte[] sampleTextBytes = text.getBytes();
			qEncoderStream.write(sampleTextBytes, 0, sampleTextBytes.length);
			qEncoderStream.flush();
			String qEncodedText = outStream.toString();
			qEncoderStream.close();
			str = "=?UTF-8?Q?" + qEncodedText + "?=";
			Assert.assertEquals(text, client.decodeText(str));

			Assert.assertEquals(text, client.decodeText(text));

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
