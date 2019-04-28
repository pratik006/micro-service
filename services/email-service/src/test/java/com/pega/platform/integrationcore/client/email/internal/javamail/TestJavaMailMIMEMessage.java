package com.pega.platform.integrationcore.client.email.internal.javamail;

import com.pega.platform.integrationcore.client.email.EmailClient;
import com.pega.platform.integrationcore.client.email.EmailClientException;
import com.pega.platform.integrationcore.client.email.EmailClientRuntimeException;
import com.pega.platform.integrationcore.client.email.MIMEMessage;
import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;
import com.pega.platform.integrationcore.utils.IntegrationTestUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
/**
 * Unit tests for various methods in JavaMailMIMEMessage.java class
 * 
 * @author Unifiers
 *
 */

public class TestJavaMailMIMEMessage {
	private static final char SEPARATOR = File.separatorChar;
	private static final String FILE_NAME = "Pega-logo.jpg";
	private static final String SAMPLE_TEXT = "Sample TEXT for test!";
	private static final String CONTENT_ID = "<part00909@utopia.example>";

	private static final String RESOURCES_PATH = System.getProperty("user.dir") + SEPARATOR + "src" + SEPARATOR
			+ "test" + SEPARATOR + "resources" + SEPARATOR;

	private static final Logger slf4jLog = LoggerFactory.getLogger(TestJavaMailMIMEMessage.class);
	EmailClient client = null;
	MIMEMessage message = null;

	private static X509Certificate cert = null;
	private static PrivateKey privateKey = null;

	@BeforeClass
	public static void initTestClass() {
		Security.addProvider(new BouncyCastleProvider());

		cert = getTestCertificate();
	}

	@Before
	public void setupEmailClient() {
		client = new JavaEmailClient();
		SampleEmailClientConfiguration clientconfig=new SampleEmailClientConfiguration();
		try {
			client.initialize(clientconfig.getClientConfiguration());
			client.connect(false, null);
			message = client.createMessage(null);
		} catch (EmailClientException e) {
			slf4jLog.error(e.getMessage());
		}
	}

	@Test
	public void testSetText() {
		try {
			message.setText(SAMPLE_TEXT);
			Assert.assertEquals(SAMPLE_TEXT, message.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetTextWithParameters() {
		try {
			message.setText(SAMPLE_TEXT, StandardCharsets.UTF_16.name(), "text");
			Assert.assertEquals(SAMPLE_TEXT, message.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetContentID() {
		try {
			message.setContentID(CONTENT_ID);
			Assert.assertEquals(CONTENT_ID, message.getContentID());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetFileName() {
		try {
			message.setFileName(FILE_NAME);
			Assert.assertEquals(FILE_NAME, message.getFileName());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetDisposition() {
		try {
			message.setDisposition(MIMEPart.ContentDisposition.ATTACHMENT);
			Assert.assertEquals(MIMEPart.ContentDisposition.ATTACHMENT, message.getDisposition());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetAndAddHeader() {
		try {
			String headerName = "Accept";
			String[] headerValues = { "text/plain", "text/xml" };

			message.setHeader(headerName, headerValues[0]);
			message.addHeader(headerName, headerValues[1]);

			String[] obtainedHeader = message.getHeader(headerName);

			Assert.assertArrayEquals(headerValues, obtainedHeader);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetAndGetRecipient() {
		try {
			MIMEMessage.RecipientType type = MIMEMessage.RecipientType.TO;
			List<String> expected = Arrays.asList("a@b", "c@d");
			message.setRecipient(type, expected);
			List<String> actualRecipients = message.getRecipients(type);
			Assert.assertEquals(expected, actualRecipients);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllRecipients() {
		try {
			List<String> expected = Arrays.asList("a1@b1", "c1@d1", "a2@b2", "c2@d2", "a3@b3", "c3@d3");
			List<String> addressesTo = Arrays.asList("a1@b1", "c1@d1");
			List<String> addressesCc = Arrays.asList("a2@b2", "c2@d2");
			List<String> addressesBcc = Arrays.asList("a3@b3", "c3@d3");
			message.setRecipient(MIMEMessage.RecipientType.TO, addressesTo);
			message.setRecipient(MIMEMessage.RecipientType.CC, addressesCc);
			message.setRecipient(MIMEMessage.RecipientType.BCC, addressesBcc);

			List<String> actualRecipients = message.getAllRecipients();
			Assert.assertEquals(expected, actualRecipients);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSubjectDescription() {
		try {
			message.setSubject("Sample email subject!");
			message.setDescription("Email Description");
			Assert.assertEquals("Sample email subject!", message.getSubject());
			Assert.assertEquals("Email Description", message.getDescription());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSentDate() {
		try {
			Date dummyDate = new Date();
			message.setSentDate(dummyDate);
			Assert.assertEquals(dummyDate.toString(), message.getSentDate().toString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	} 

	@Test
	public void testFromAddress() {
		try { 
			message.setFrom("fake_sender_id@dummy.com");
			Assert.assertEquals("fake_sender_id@dummy.com", message.getFrom());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		try {
			MIMEMessage message2 = client.createMessage(null);
			message2.setFrom("fake_sender_id@dummy.com", "fake sender name");
			Assert.assertEquals("fake_sender_id@dummy.com", message.getFrom());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testIsMimeType() {
		try {
			// Testing valid and invalid mime types
			message.setSubject("Sample multipart email subject!");
			message.saveChanges();
			// Testing for simple text plain message mimetype
			Assert.assertEquals(true, message.isMimeType("text/plain"));

			// Checking for multipart messages
			MIMEPart part = client.createPart();
			MIMEMultipart multipart = client.createMultipart(null);
			MIMEMessage message2 = client.createMessage(null);
			part.setText("Sample text body as Part!");
			part.setText("Setting INLINE MimeBody Part");
			part.setContentID("1001");
			part.setDisposition(MIMEPart.ContentDisposition.INLINE);
			multipart.addBodyPart(part);
			message2.setContent(multipart);
			message2.saveChanges();

			Assert.assertEquals(true, message2.isMimeType("multipart/*"));
			Assert.assertEquals(false, message2.isMimeType("type/subtype"));
		} catch (Exception e) {
			slf4jLog.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testReplyTo() {
		try {
			// Testing valid and invalid mime types
			message.setReplyTo("Chilkuru Ragunath <chilr1@pega.com>,tatir@pega.com,chil@abc.com");
			Assert.assertEquals("Chilkuru Ragunath <chilr1@pega.com>", message.getReplyTo());
			message.setReplyTo("abc@gmail.com,cbd@google.com");
			Assert.assertEquals("abc@gmail.com", message.getReplyTo());
			message.setReplyTo("<chilr1@pega.com>");
			Assert.assertEquals("chilr1@pega.com", message.getReplyTo());
			// System.out.println("The getReplyto response is "
			// +message.getReplyTo());

		} catch (Exception e) {
			slf4jLog.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

		try {
			// Testing for email exception incase of invalid address. Since only
			// comma seperated email addresses are supported.
			MIMEMessage message2 = client.createMessage(null);
			message2.setReplyTo("abc@gmail.com;cbd@google.com");
			Assert.fail();

		} catch (Exception e) {
			Assert.assertTrue("EmailClientRuntimeException".equals(e.getClass().getSimpleName()));
		}

		try {
			// Testing for email exception incase of invalid address. Since only
			// comma seperated valid email addresses are supported.
			MIMEMessage message3 = client.createMessage(null);
			message3.setReplyTo("Chilkuru Raghunath");
			Assert.fail();

		} catch (EmailClientRuntimeException e) {
			Assert.assertTrue("EmailClientRuntimeException".equals(e.getClass().getSimpleName()));
		} catch (EmailClientException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testHeaderLines() {
		try {

			String[] line = new String[2];
			line[0] = "Content-Type: text/plain; charset=us-ascii";
			line[1] = "New header linea";
			message.addHeaderLine(line[0]);
			message.addHeaderLine(line[1]);
			List<String> ll = Collections.list(message.getAllHeaderLines());
			Assert.assertArrayEquals(line, ll.toArray());

			Assert.assertEquals(1, message.getHeader("Content-Type").length);
			Assert.assertEquals("text/plain; charset=us-ascii", message.getHeader("Content-Type")[0]);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetAllHeaders() {
		try {
			String headerName1 = "Accept";
			String headerName2 = "Authorization";
			String[] headerValues = { "text/plain", "text/xml", "Basic", "OAuth 2.0" };

			message.setHeader(headerName1, headerValues[0]);
			message.addHeader(headerName1, headerValues[1]);
			message.setHeader(headerName2, headerValues[2]);
			message.addHeader(headerName2, headerValues[3]);

			Part part = new MimeBodyPart();
			String rfc822headers = "SampleHeaderName1:Sample header value 1" + System.lineSeparator()
					+ "SampleHeaderName2:Sample header value 2;Sample header value 2" + System.lineSeparator()
					+ "SampleHeaderName3:Sample header value 3";
			part.setContent(rfc822headers, "text/plain");

			message.setContent(part.getInputStream(), "text/RFC822-headers");

			Map<String, String> allHeaders = message.getAllHeaders(false);
			Assert.assertEquals("text/plain;text/xml", allHeaders.get("Accept"));
			Assert.assertEquals("Basic;OAuth 2.0", allHeaders.get("Authorization"));

			allHeaders = message.getAllHeaders(true);
			System.out.println("allHeaders : " + allHeaders);
			Assert.assertEquals("Sample header value 1", allHeaders.get("SampleHeaderName1"));
			Assert.assertEquals("Sample header value 2;Sample header value 2", allHeaders.get("SampleHeaderName2"));
			Assert.assertEquals("Sample header value 3", allHeaders.get("SampleHeaderName3"));

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentAsMimeMessage() {
		try {
			message.getContentAsMimeMessage();
			Assert.fail();

		} catch (UnsupportedOperationException e) {
			Assert.assertTrue("UnsupportedOperationException".equals(e.getClass().getSimpleName()));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWriteTo() {
		JavaMailMIMEMessage ms = null;
		try {
			ms = new JavaMailMIMEMessage(new MimeMessage(null, new ByteArrayInputStream((SAMPLE_TEXT).getBytes())));
		} catch (MessagingException e) {
			slf4jLog.error(e.getMessage(), e);
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ms.writeTo(stream);
		Assert.assertEquals(SAMPLE_TEXT, (new String(stream.toByteArray())).trim());
	}

	@Test
	public void testGetSize() {
		JavaMailMIMEMessage ms = null;
		try {
			ms = new JavaMailMIMEMessage(
					new MimeMessage(null, new ByteArrayInputStream((System.lineSeparator() + SAMPLE_TEXT).getBytes())));
		} catch (MessagingException e) {
			slf4jLog.error(e.getMessage(), e);
		}
		Assert.assertEquals(SAMPLE_TEXT.length(), ms.getSize());

	}

	@Test
	public void testGetCharset() {
		try {
			Assert.assertEquals(Charset.defaultCharset().toString().toLowerCase(), message.getCharset().toLowerCase());
			message.setCharset(StandardCharsets.US_ASCII.name());
			message.setText("Sample email text");
			message.saveChanges();
			Assert.assertEquals(StandardCharsets.US_ASCII.name().toLowerCase(), message.getCharset().toLowerCase());

		} catch (Exception e) {
			slf4jLog.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetMessageContentAsStream() {
		try {
			message.setFrom("fake_sender_id@dummy.com");
			message.setText(SAMPLE_TEXT);

			InputStream stream = message.getContentAsStream();

			byte[] dataBytes = new byte[SAMPLE_TEXT.length()];

			stream.read(dataBytes);
			String s = new String(dataBytes, StandardCharsets.UTF_8.name());
			Assert.assertEquals(SAMPLE_TEXT, s);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testDataHandlerBytes() {
		try {
			message.setDataHandler(SAMPLE_TEXT.getBytes(), "SampleTextFile.txt");
			Assert.assertEquals(SAMPLE_TEXT, message.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testDataHandlerText() {
		try {
			message.setDataHandler(SAMPLE_TEXT, "text/plain");
			Assert.assertEquals(SAMPLE_TEXT, message.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentType() {
		try {
			message.setText(SAMPLE_TEXT);
			Assert.assertEquals("text/plain", message.getContentType());

			MIMEMultipart mainMultipart_mixed = client.createMultipart(null);

			MIMEPart textBodyPart = client.createPart();
			textBodyPart.setText("Sample text body as Part!");
			mainMultipart_mixed.addBodyPart(textBodyPart);

			MIMEPart imageAttachPart = client.createPart();
			byte[] fileData = IntegrationTestUtils.getFileData(RESOURCES_PATH + FILE_NAME);
			imageAttachPart.setDataHandler(fileData, FILE_NAME);
			imageAttachPart.setDisposition(MIMEPart.ContentDisposition.ATTACHMENT);
			imageAttachPart.setFileName(FILE_NAME);

			mainMultipart_mixed.addBodyPart(imageAttachPart);
			message.setContent(mainMultipart_mixed);
			message.saveChanges();

			Assert.assertTrue("Content-Type should contain multipart/mixed",
					message.getContentType().toLowerCase().contains("multipart/mixed"));

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetInputStream() {
		try {
			MIMEMultipart multipart = client.createMultipart(null);
			MIMEPart part = client.createPart();
			part.setText(SAMPLE_TEXT);
			part.setHeader("Content-type", "text/plain");
			multipart.addBodyPart(part);
			message.setContent(multipart);
			InputStream ioStream = message.getInputStream();

			byte[] dataArray = new byte[4096];
			ioStream.read(dataArray);
			String str = new String(dataArray);

			Assert.assertTrue("Inputstream content : " + str + "   doesn't contain : " + SAMPLE_TEXT, str.contains(SAMPLE_TEXT));
			Assert.assertTrue("Inputstream should return content-type", str.contains("text/plain"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetAndGetContentAsMultipart() {
		try {
			MIMEMultipart mimeMultipart = client.createMultipart(null);
			MIMEPart mimePart = client.createPart();
			mimePart.setText(SAMPLE_TEXT);
			mimeMultipart.addBodyPart(mimePart);

			MIMEPart imagePart = client.createPart();
			imagePart.setDataHandler(SAMPLE_TEXT.getBytes(), "SampleFile.txt");
			mimeMultipart.addBodyPart(imagePart);

			message.setContent(mimeMultipart);

			MIMEMultipart mPart = message.getContentAsMimeMultipart();
			Assert.assertEquals(2, mPart.getPartsCount());

			MIMEPart retrievedPart = mPart.getBodyPart(0);
			Assert.assertEquals(SAMPLE_TEXT, retrievedPart.getContentAsString());

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		try {
			MIMEMessage message2 = client.createMessage(null);
			// Testing if simple mail message results in an exception
			message2.setSubject("Sample plain email subject!");
			message2.saveChanges();
			message2.getContentAsMimeMultipart();
			Assert.fail();
		} catch (EmailClientRuntimeException e) {
			slf4jLog.info(e.getMessage(), e);
			Assert.assertTrue(e.getMessage().contains("No MimeMessage content"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetMultipartContent() {
		try {
			MIMEMultipart mimeMultipart = client.createMultipart(null);
			MIMEPart mimePart = client.createPart();
			mimePart.setDisposition(MIMEPart.ContentDisposition.ATTACHMENT);
			mimePart.setFileName(FILE_NAME);
			mimePart.setContentID(CONTENT_ID);
			mimePart.setText("Testing MimeMessage setContent");
			mimeMultipart.addBodyPart(mimePart);
			message.setContent(mimeMultipart);
			MIMEMultipart mPart = message.getContentAsMimeMultipart();
			Assert.assertEquals("Text should be Equal", "Testing MimeMessage setContent",
					mPart.getBodyPart(0).getContentAsString());
			Assert.assertEquals("Disposition should be attachment", MIMEPart.ContentDisposition.ATTACHMENT,
					mPart.getBodyPart(0).getDisposition());
			Assert.assertEquals("Filename should be Pega-logo.jpg", FILE_NAME, mPart.getBodyPart(0).getFileName());
			Assert.assertEquals("ContentID should be same", CONTENT_ID, mPart.getBodyPart(0).getContentID());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetObjectContent() {
		try {
			message.setContent(SAMPLE_TEXT, "text/plain");
			Assert.assertEquals(SAMPLE_TEXT, message.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetReplyMessage() {
		try {
			List<String> addressTo = Arrays.asList("fake_receiver_id@dummy.com");
			message.setRecipient(MIMEMessage.RecipientType.TO, addressTo);
			message.setSubject("Sample simple email subject!");
			message.setText(SAMPLE_TEXT);
			Assert.assertEquals("Re: Sample simple email subject!", message.getReplyMessage(false).getSubject());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetAsSeen() {
		try {
			message.setText(SAMPLE_TEXT);
			message.setAsSeen();

			Assert.assertTrue(message.isSeen());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSetAsDeleted() {
		try {
			message.setText(SAMPLE_TEXT);
			message.setAsDeleted();
			Assert.assertTrue(message.isDeleted());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSaveChanges() {
		try {
			message.setText(SAMPLE_TEXT);
			Assert.assertNull(message.getHeader("Message-ID"));
			Assert.assertNull(message.getHeader("Content-type"));
			message.saveChanges();
			Assert.assertNotNull(message.getHeader("Message-ID"));
			Assert.assertNotNull(message.getHeader("Content-type"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testMessageSigning() {
		try {
			List<String> addressTo = Arrays.asList("fake_receiver_id@dummy.com");
			message.setRecipient(MIMEMessage.RecipientType.TO, addressTo);
			message.setSubject("Sample multipart email subject!");
			message.setText(SAMPLE_TEXT);
			List<X509Certificate> tt = new ArrayList<>();
			tt.add(cert);
			MIMEMultipart signedMultiPart = message.getAsSignedMultipart(tt, privateKey);

			Assert.assertTrue("Signed message should have 2 parts", signedMultiPart.getPartsCount() == 2);

			MIMEPart textBodyPart = signedMultiPart.getBodyPart(0);
			Assert.assertEquals(SAMPLE_TEXT, textBodyPart.getContentAsString().trim());

			MIMEPart signedPart = signedMultiPart.getBodyPart(1);

			Assert.assertTrue("Message signing failed", signedPart.isMimeType("application/pkcs7-signature"));

			String contentTypeName = signedPart.getContentType();
			ContentType cType = new ContentType(contentTypeName);

			String smime_type = cType.getParameter("smime-type");
			Assert.assertEquals("signed-data", smime_type);

			String smime_name = cType.getParameter("name");
			Assert.assertEquals("smime.p7s", smime_name);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testMessageEncryptionAndDecryption() {
		MIMEMessage encryptedMessage = null;
		try {
			List<String> addressTo = Arrays.asList("fake_receiver_id@dummy.com");
			message.setRecipient(MIMEMessage.RecipientType.TO, addressTo);
			message.setSubject("Sample simple email subject!");
			message.setText(SAMPLE_TEXT);

			List<X509Certificate> certs = new ArrayList<>();
			certs.add(cert);

			MIMEPart encryptedPart = message.getAsEncryptedPart(certs);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			encryptedPart.writeTo(out);
			encryptedMessage = client.createMessage(new ByteArrayInputStream(out.toByteArray()));

			Enumeration headers = ((MimeMessage) message.getWrappedObject()).getAllHeaderLines();
			while (headers.hasMoreElements()) {
				String headerLine = (String) headers.nextElement();
				if (!Strings.toLowerCase(headerLine).startsWith("content-")) {
					encryptedMessage.addHeaderLine(headerLine);
				}
			}

			Assert.assertTrue("Message should be encrypted!", (encryptedMessage.isMimeType("application/pkcs7-mime")
					|| encryptedMessage.isMimeType("application/x-pkcs7-mime")));

			String contentTypeName = encryptedMessage.getContentType();
			ContentType cType = new ContentType(contentTypeName);

			String smime_type = cType.getParameter("smime-type");
			Assert.assertEquals("enveloped-data", smime_type);
			Assert.assertEquals("Sample simple email subject!", encryptedMessage.getSubject());

			// try decryption -
			InputStream is = encryptedMessage.getAsDecryptedStream(cert, privateKey);
			MIMEMessage mes = client.createMessage(is);
			Assert.assertEquals(SAMPLE_TEXT, mes.getContentAsString().trim());

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private static X509Certificate getTestCertificate() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

			keyGen.initialize(1024);

			KeyPair key = keyGen.generateKeyPair();
			X500Name subjectDN = new X500Name("CN=test");

			BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
			Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
			Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);

			SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo.getInstance(key.getPublic().getEncoded());

			X509v1CertificateBuilder builder = new X509v1CertificateBuilder(subjectDN, serialNumber, startDate, endDate,
					subjectDN, subPubKeyInfo);

			privateKey = key.getPrivate();

			ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);

			X509CertificateHolder holder = builder.build(signer);
			cert = new JcaX509CertificateConverter().getCertificate(holder);

		} catch (Exception e) {
			slf4jLog.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		return cert;
	}



}
