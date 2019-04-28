package com.pega.platform.integrationcore.client.email.internal.javamail;


import com.pega.platform.integrationcore.client.email.EmailClient;
import com.pega.platform.integrationcore.client.email.EmailClient.ProfileType;
import com.pega.platform.integrationcore.client.email.EmailClientResponse;
import com.pega.platform.integrationcore.client.email.MIMEMessage;
import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;
import com.pega.platform.integrationcore.client.email.config.EmailClientConfiguration;
import com.pega.platform.integrationcore.client.email.config.EmailReceiverProfile;
import com.pega.platform.integrationcore.client.email.config.EmailSenderProfile;
import com.pega.platform.integrationcore.client.email.internal.javamail.stubs.MailboxStub;
import com.pega.platform.integrationcore.utils.IntegrationTestUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
/**
 * Class level tests for EmailClient.
 * Uses stubs for EmalServer objects/connectivity
 * 
 * @author Unifiers
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJavaEmailClientUsingStubs 
{
	private static final char SEPARATOR = File.separatorChar;
	private static final String RESOURCES_PATH = System.getProperty("user.dir")
			+ SEPARATOR + "src" + SEPARATOR + "test" + SEPARATOR + "resources" + SEPARATOR;
	private static final String FILE_NAME = "Pega-logo.jpg";
	private static final Logger slf4jLog = LoggerFactory.getLogger(TestJavaEmailClientUsingStubs.class);
	private EmailClient client = null;
	private List<MIMEMessage> messages = null;
	
	SampleEmailClientConfiguration clientconfig=new SampleEmailClientConfiguration();
	@Test
	public void testC_SendMultipartEmail() {	    
		testMultiPartEmailSent("ATTACHMENT");
	}
	
	@Test
	public void testD_ReceiveMultipartEmail() {		
		testMultiPartMailRecvd("ATTACHMENT");
	}

	// @PegaTestCase(FeatureID="FR-421", TCID="TC-210809")
	@Test
	public void testE_SimpleEmailRetrievalWithAutoDisconnectAndUnread() {	
		SimpleEmailRetrieval(true);
	
	}
	// @PegaTestCase(FeatureID="FR-421", TCID="TC-210810")
	@Test
	public void testG_SimpleEmailRetrievalWithoutUnread() {		
		SimpleEmailRetrieval(false);
	}

	// @PegaTestCase(FeatureID="FR-421", TCID="TC-210808")
	@Test
	public void testH_SimpleEmailSendAndReceiveWithoutAuthentication() {
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(getInvalidClientConfigurationSender());
			client.connect(false, null);
			MIMEMessage message=clientconfig.prepareMessage(client,"Sample simple email subject!");			
			message.setText("Sample simple email TEXT!");
			response = client.sendEmail(message, true);

			client.initialize(getClientConfigurationReceiver());
			client.connect(false, null);
			messages = client.retrieveEmails(true, false);
		} catch (Exception e) {
			slf4jLog.error("Exception in testH_ReceiveSimpleEmail", e);
			Assert.fail(e.toString());
		}
		clientconfig.verifyResponse(response);
		Assert.assertNotNull("Messages should not be null", messages);
		Assert.assertEquals("Must have received 1 mail", 1, messages.size());//NOSONAR used to exclude false-positive

		MIMEMessage message = messages.get(0);
		clientconfig.verifySimpleEmailProperties(message);
		MailboxStub.clearAll();
		client.disconnect();
	}

	// @PegaTestCase(FeatureID="FR-421", TCID="TC-210908")
	@Test
	public void testI_ConnectivityWithAuthenticationForSenderWithBothConfigurations() {
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(clientconfig.getClientConfiguration());
			response = client.testConnection(ProfileType.SENDER);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		verifySessionPropertiesWithTransport(response);
	}

	// @PegaTestCase(FeatureID="FR-421", TCID="TC-210909")
	@Test
	public void testJ_ConnectivityWithAuthenticationForReceiverWithBothConfigurations() {
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(clientconfig.getClientConfiguration());
			response = client.testConnection(ProfileType.RECEIVER);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		 verifySessionProperties(response);
	}

	// @PegaTestCase(FeatureID="FR-421", TCID="TC-211026")
	@Test
	public void testK_ConnectivityWithAuthenticationForSenderWithOnlySenderConfigurations() {
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(getClientConfigurationSender());
			response = client.testConnection(ProfileType.SENDER);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		verifySessionPropertiesWithTransport(response);
	}

	// @PegaTestCase(FeatureID="FR-421", TCID="TC-211027")
	@Test
	public void testL_ConnectivityWithAuthenticationForReceiverWithOnlyReceiverConfigurations() {
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(getClientConfigurationReceiver());
			response = client.testConnection(ProfileType.RECEIVER);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		 verifySessionProperties(response);
	}

	@Test
	public void testO_SendMultipartEmailWithInlineAttachment() {		
		testMultiPartEmailSent("INLINE");
	}

	@Test
	public void testP_ReceiveMultipartEmailWithInlineAttachment() {
		testMultiPartMailRecvd("INLINE");
	}

	@Test
	public void testQ_SendMultipartEmailWithAlternateContentAndAttachments() {
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(clientconfig.getClientConfiguration());
			client.connect(false, null);
			MIMEMessage message=clientconfig.prepareMessage(client,"Sample multipart email subject!");		
			MIMEMultipart mainMultipart_mixed = client.createMultipart(null);

			MIMEMultipart mainMultipart_alternate = client.createMultipart("alternate");

			MIMEPart textBodyPart = client.createPart();
			textBodyPart.setText("Sample text body as Part!");
			mainMultipart_alternate.addBodyPart(textBodyPart, 0);

			MIMEPart htmlBodyPart = client.createPart();
			// htmlBodyPart.setText("<html><body>Sample text body as
			// Part!</body></html>");
			htmlBodyPart.setText("<html><body>Sample text body as Part!</body></html>", "utf-8", "html");
			mainMultipart_alternate.addBodyPart(htmlBodyPart, 1);

			MIMEPart imageAttachPart = client.createPart();
			byte[] fileData = IntegrationTestUtils.getFileData(RESOURCES_PATH + FILE_NAME);
			imageAttachPart.setDataHandler(fileData, FILE_NAME);
			imageAttachPart.setDisposition(MIMEPart.ContentDisposition.INLINE);
			imageAttachPart.setFileName(FILE_NAME);

			MIMEPart alternatePart = client.createPart();
			alternatePart.setContent(mainMultipart_alternate);

			mainMultipart_mixed.addBodyPart(alternatePart);
			mainMultipart_mixed.addBodyPart(imageAttachPart);
			

			message.setContent(mainMultipart_mixed);

			response = client.sendEmail(message, true);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}

		clientconfig.verifyResponse(response);
		Assert.assertTrue("Authentication should have used", response.isAuthenticationUsed());
	}

	@Test
	public void testR_ReceiveMultipartEmailWithAlternateContentAndAttachments() {
		clientconfig.verifyMessagesArrival();
		MIMEMessage message = clientconfig.messages.get(0);
		MIMEMultipart multipart = verifyMultipartEmailProperties(message);
		MIMEPart alternatePart = multipart.getBodyPart(0);
		Assert.assertTrue("Message Content-Type should be multipart/mixed", message.isMimeType("multipart/mixed"));		 
		MIMEMultipart multiPartAlternate = alternatePart.getContentAsMimeMultipart();
		verifyTextPart(multiPartAlternate);
		verifyHTMLPart(multiPartAlternate);
		verifyImagePart(multipart,"INLINE");
		message.setAsSeen();
		clientconfig.client.disconnect();
	}

	private EmailClientConfiguration getClientConfigurationSender() {
		EmailSenderProfile senderProfile =clientconfig.setSenderProfile();
		EmailClientConfiguration config = new EmailClientConfiguration();
		config.setSenderProfile(senderProfile);

		return config;
	}

	private EmailClientConfiguration getInvalidClientConfigurationSender() {
		EmailSenderProfile senderProfile = new EmailSenderProfile();
		senderProfile.setHost("dummy.smtp.mail.com");
		senderProfile.setPort(99);
		// senderProfile.setUserId("fake_sender_id@dummy.com");
		// senderProfile.setUserPassword("fake_pwd");
		senderProfile.setProtocol("stub_smtp");

		EmailClientConfiguration config = new EmailClientConfiguration();
		config.setSenderProfile(senderProfile);

		return config;
	}

	private EmailClientConfiguration getClientConfigurationReceiver() {
		EmailReceiverProfile receiverProfile = clientconfig.setReceiverProfile();
		EmailClientConfiguration config = new EmailClientConfiguration();
		config.setReceiverProfile(receiverProfile);

		return config;
	}
	
	private void SimpleEmailRetrieval(boolean unread)
	{		
		EmailClientResponse response = null;		
		MIMEMessage message = null;
		try {
			client = new JavaEmailClient();
			client.initialize(clientconfig.getClientConfiguration());
			client.connect(false, null);
			message=clientconfig.prepareMessage(client,"Sample simple email subject!");
			message.setText("Sample simple email TEXT!");
			response = client.sendEmail(message, true); // autodisconnecting
														// sender after sending
														// email
			Assert.assertNotNull("Response should not be null", response);
			Assert.assertNotNull("Message-ID should not be null", response.getMessageId());//NOSONAR used to exclude false-positive

			messages = client.retrieveEmails(unread, false); // retrieving only
															// unread emails
															// without
															// autodisconnecting
															// as we need to
															// check for
															// assertion

		} catch (Exception e) {
			slf4jLog.error(e.getMessage(), e);
			Assert.fail(e.toString());
		}

		Assert.assertEquals("Must have received 1 mail", 1, messages.size());//NOSONAR used to exclude false-positive
		message = messages.get(0);
		clientconfig.verifySimpleEmailProperties(message); 
		MailboxStub.clearAll();
		client.disconnect();
	}
	private void testMultiPartEmailSent(String disposition)
	{
		EmailClientResponse response = null;
		try {
			client = new JavaEmailClient();
			client.initialize(clientconfig.getClientConfiguration());
			client.connect(false, null);
			MIMEMessage message=clientconfig.prepareMessage(client,"Sample multipart email subject!");			
			MIMEMultipart mainMultipart_mixed = client.createMultipart(null);

			MIMEPart textBodyPart = client.createPart();
			textBodyPart.setText("Sample text body as Part!");
			mainMultipart_mixed.addBodyPart(textBodyPart, 0);

			MIMEPart imageAttachPart = client.createPart();
			byte[] fileData = IntegrationTestUtils.getFileData(RESOURCES_PATH + FILE_NAME);
			imageAttachPart.setDataHandler(fileData, FILE_NAME);
			imageAttachPart.setDisposition(MIMEPart.ContentDisposition.valueOf(disposition));
			imageAttachPart.setFileName(FILE_NAME);
			mainMultipart_mixed.addBodyPart(imageAttachPart);
			message.setContent(mainMultipart_mixed);
			response =client.sendEmail(message, true);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
		clientconfig.verifyResponse(response);
		Assert.assertTrue("Authentication should have used", response.isAuthenticationUsed());
	}
	private MIMEMultipart verifyMultipartEmailProperties(MIMEMessage message)
	{
		Assert.assertNotNull("Message-ID should not be null", message.getHeader("Message-ID"));
		Assert.assertEquals("fake_sender_id@dummy.com", message.getFrom());
		Assert.assertEquals("Sample multipart email subject!", message.getSubject());		
		MIMEMultipart multipart = message.getContentAsMimeMultipart();
		Assert.assertTrue("Should contain 2 parts", multipart.getPartsCount() == 2);
        return multipart;
	}
	private void verifySessionProperties(EmailClientResponse a_response)
	{
		Assert.assertTrue("Session should have established",a_response.isSessionEstablished());//NOSONAR used to exclude false-positive
		Assert.assertTrue("Should have connected to SMTP server", a_response.isConnected());
		Assert.assertTrue("Should have disconnect from SMTP server",a_response.isDisconnected());	
		
	}
	private void verifySessionPropertiesWithTransport(EmailClientResponse a_response)
	{
		verifySessionProperties(a_response);
		Assert.assertTrue("Transport should have acquired", a_response.isTransportAcquired());
	}
	private void verifyImagePart(MIMEMultipart multipart,String disposition)
	{
		MIMEPart imageAttachPart = multipart.getBodyPart(1);
		Assert.assertTrue("Content-Type should contain image/jpeg",
				imageAttachPart.getContentType().toLowerCase().contains("image/jpeg"));
		Assert.assertEquals("Pega-logo.jpg", imageAttachPart.getFileName());
		Assert.assertEquals(MIMEPart.ContentDisposition.valueOf(disposition), imageAttachPart.getDisposition());
		Assert.assertTrue("Should contain some bytes", imageAttachPart.getSize() > 100);		
	}
	private void verifyTextPart(MIMEMultipart multipart)
	{
		MIMEPart textBodyPart = multipart.getBodyPart(0);
		Assert.assertTrue("Content-Type should contain text/plain",
				textBodyPart.getContentType().toLowerCase().contains("text/plain"));
		Assert.assertEquals("Sample text body as Part!", textBodyPart.getContentAsString().trim());
	}
	private void verifyHTMLPart(MIMEMultipart multipart)
	{
		MIMEPart htmlPart = multipart.getBodyPart(1);
		Assert.assertTrue("Content-Type should contain text/html",
				htmlPart.getContentType().toLowerCase().contains("text/html"));
		Assert.assertEquals("<html><body>Sample text body as Part!</body></html>",
				htmlPart.getContentAsString().trim());
	}
	private void testMultiPartMailRecvd(String disposition)
	{
		clientconfig.verifyMessagesArrival();	
	    MIMEMessage message = clientconfig.messages.get(0);
	    MIMEMultipart multipart=verifyMultipartEmailProperties(message);
	    Assert.assertTrue("Content-Type should contain multipart", 
				message.isMimeType("multipart/*"));
		verifyTextPart(multipart);
		verifyImagePart(multipart,disposition);
		message.setAsSeen();
		clientconfig.client.disconnect();
 
	}
}

