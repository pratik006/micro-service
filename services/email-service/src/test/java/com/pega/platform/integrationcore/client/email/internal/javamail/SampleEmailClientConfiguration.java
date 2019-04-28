package com.pega.platform.integrationcore.client.email.internal.javamail;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

import com.pega.platform.integrationcore.client.email.EmailClient;
import com.pega.platform.integrationcore.client.email.EmailClientException;
import com.pega.platform.integrationcore.client.email.EmailClientResponse;
import com.pega.platform.integrationcore.client.email.MIMEMessage;
import com.pega.platform.integrationcore.client.email.config.EmailClientConfiguration;
import com.pega.platform.integrationcore.client.email.config.EmailReceiverProfile;
import com.pega.platform.integrationcore.client.email.config.EmailSenderProfile;

public class SampleEmailClientConfiguration {
	protected EmailClient client = null;
	protected List<MIMEMessage> messages = null;
	protected EmailClientConfiguration getClientConfiguration() {
		EmailSenderProfile senderProfile=setSenderProfile();
		senderProfile.setUseSSL(true);
		EmailReceiverProfile receiverProfile=setReceiverProfile();
		EmailClientConfiguration config = new EmailClientConfiguration();
		config.setSenderProfile(senderProfile);
		config.setReceiverProfile(receiverProfile);
		return config;
	}
	protected EmailSenderProfile setSenderProfile() {
		EmailSenderProfile senderProfile = new EmailSenderProfile();
		senderProfile.setHost("dummy.smtp.mail.com");
		senderProfile.setPort(99);
		senderProfile.setUserId("fake_sender_id@dummy.com");
		senderProfile.setUserPassword("fake_pwd");
		senderProfile.setProtocol("stub_smtp");
		return senderProfile;
    }
	protected EmailReceiverProfile setReceiverProfile() {
		EmailReceiverProfile receiverProfile = new EmailReceiverProfile();
		receiverProfile.setProtocol("stub_imap");
		receiverProfile.setHost("dummy.imap.mail.com");
		receiverProfile.setPort(999);
		receiverProfile.setUserId("fake_receiver_id@dummy.com");
		receiverProfile.setUserPassword("fake_pw");
		receiverProfile.setUseSSL(true);
		receiverProfile.setFolderName("INBOX");
		return receiverProfile;
	}
	protected void verifyMessagesArrival()
	{
	 try {
		client = new JavaEmailClient();
		client.initialize(getClientConfiguration());
			client.connect(false, null);
			messages = client.retrieveEmails(true, false);
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	    Assert.assertNotNull("Messages should not be null", messages);
		Assert.assertEquals("Must have received 1 mail", 1,messages.size());//NOSONAR used to exclude false-positive
	}
	protected void verifyResponse(EmailClientResponse a_response){
		Assert.assertNotNull("Response should not be null", a_response);
		Assert.assertNotNull("Message-ID should not be null",a_response.getMessageId());//NOSONAR used to exclude false-positive		
		Assert.assertTrue("Session should have established",a_response.isSessionEstablished());
		Assert.assertTrue("Transport should have acquired",a_response.isTransportAcquired());
		Assert.assertTrue("Should have connected to SMTP server",a_response.isConnected());
		Assert.assertTrue("Should have disconnect from SMTP server after send email", a_response.isDisconnected());
		
	}
	protected void verifySimpleEmailProperties(MIMEMessage message)
	{
		Assert.assertNotNull("Message-ID should not be null", message.getHeader("Message-ID"));
		Assert.assertEquals("fake_sender_id@dummy.com", message.getFrom());
		Assert.assertEquals("Sample simple email subject!", message.getSubject());
		Assert.assertEquals("Sample simple email TEXT!", message.getContentAsString().trim());
		Assert.assertTrue("Content-Type should contain text/plain",
				message.getContentType().toLowerCase().contains("text/plain"));	
	}
	protected MIMEMessage prepareMessage(EmailClient client,String subject) throws EmailClientException
	{
		MIMEMessage message = client.createMessage(null);
		message.setFrom("fake_sender_id@dummy.com");
		List<String> addressTo = Arrays.asList("fake_receiver_id@dummy.com");
		message.setRecipient(MIMEMessage.RecipientType.TO, addressTo);
		message.setSubject(subject);
		return message;
	}
}
