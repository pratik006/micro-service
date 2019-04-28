package com.pega.platform.integrationcore.client.email.internal.javamail;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.pega.platform.integrationcore.client.email.MIMEMultipart;
import com.pega.platform.integrationcore.client.email.MIMEPart;

/**
 * Class level tests for JavaMail implementation of MIMEPart
 * 
 * @author Unifiers
 */
public class TestJavaMailPart {

	private static final String SAMPLE_TEXT = "Sample TEXT for test";
	private static final String SAMPLE_FILE_NAME = "SampleTextFile.txt";
	
	MIMEPart part = null;

	@Before
	public void createPart() {
		part = new JavaMailPart();
	}

	@Test
	public void testHeaders() {

		try {
			String headerName1 = "Accept";
			String headerName2 = "Authorization";
			String[] headerValues = { "text/plain", "text/xml", "Basic", "OAuth 2.0" };

			part.setHeader(headerName1, headerValues[0]);
			part.addHeader(headerName1, headerValues[1]);
			part.setHeader(headerName2, headerValues[2]);
			part.addHeader(headerName2, headerValues[3]);

			Part part2 = new MimeBodyPart();
			String rfc822headers = "SampleHeaderName1:Sample header value 1" + System.lineSeparator()
					+ "SampleHeaderName2:Sample header value 2;Sample header value 2" + System.lineSeparator()
					+ "SampleHeaderName3:Sample header value 3";
			part2.setContent(rfc822headers, "text/plain");

			part.setContent(part2.getInputStream(), "text/RFC822-headers");

			Map<String, String> allHeaders = part.getAllHeaders(false);
			Assert.assertEquals("text/plain;text/xml", allHeaders.get("Accept"));
			Assert.assertEquals("Basic;OAuth 2.0", allHeaders.get("Authorization"));

			allHeaders = part.getAllHeaders(true);
			Assert.assertEquals("Sample header value 1", allHeaders.get("SampleHeaderName1"));
			Assert.assertEquals("Sample header value 2;Sample header value 2", allHeaders.get("SampleHeaderName2"));
			Assert.assertEquals("Sample header value 3", allHeaders.get("SampleHeaderName3"));
			
			MIMEPart part3 = new JavaMailPart();
			part3.setContent("Sample content", "text/plain");
			allHeaders = part3.getAllHeaders(true);
			Assert.assertEquals(0, allHeaders.size());

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testHeaderLines() {
		String[] line = new String[2];
		line[0] = "New header line";
		line[1] = "New header linea";
		part.addHeaderLine(line[0]);
		part.addHeaderLine(line[1]);
		List<String> ll = Collections.list(part.getAllHeaderLines());
		assertArrayEquals(line, ll.toArray());
	}

	@Test
	public void testSetTextAndContentType() {
		try {
			MIMEPart textPart = new JavaMailPart();			
			textPart.setText(SAMPLE_TEXT);
			assertEquals(SAMPLE_TEXT, textPart.getContentAsString());
			assertEquals("text/plain", textPart.getContentType());

			MIMEPart htmlPart = new JavaMailPart();
			String htmlText = "<html> <body> Sample HTML text </body> </html>";
			htmlPart.setText(htmlText, StandardCharsets.UTF_8.toString(), "html");
			assertEquals(htmlText, htmlPart.getContentAsString());
			Assert.assertTrue("Content-Type should contain text/*", htmlPart.isMimeType("text/*"));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentAsMultipart() {
		String contentId = "<part00909@utopia.example>";
		try {
			MIMEMultipart mimeMultipart = new JavaMailMIMEMultipart("");
			MIMEPart mimePart = new JavaMailPart();
			mimePart.setDisposition(MIMEPart.ContentDisposition.ATTACHMENT);
			mimePart.setFileName(SAMPLE_FILE_NAME);
			mimePart.setContentID(contentId);
			mimeMultipart.addBodyPart(mimePart);
			part.setContent(mimeMultipart);

			MIMEMultipart retrievedMultipart = part.getContentAsMimeMultipart();
			Assert.assertEquals(1, retrievedMultipart.getPartsCount());

			MIMEPart retrievedPart = retrievedMultipart.getBodyPart(0);
			Assert.assertEquals(MIMEPart.ContentDisposition.ATTACHMENT, retrievedPart.getDisposition());
			Assert.assertEquals(SAMPLE_FILE_NAME, retrievedPart.getFileName());
			Assert.assertEquals(contentId, retrievedPart.getContentID());

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testDataHandler() {
		try {		
			part.setDataHandler(SAMPLE_TEXT.getBytes(), SAMPLE_FILE_NAME);

			assertEquals(SAMPLE_TEXT, part.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		try {
			MIMEPart part2 = new JavaMailPart();
			part2.setDataHandler(SAMPLE_TEXT, "text/plain");

			assertEquals(SAMPLE_TEXT, part2.getContentAsString());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWriteTo() {
		try {			
			part.setText(SAMPLE_TEXT);

			OutputStream fos = new ByteArrayOutputStream(20);
			part.writeTo(fos);

			assertEquals(SAMPLE_TEXT, fos.toString().trim());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetInputStream() {
		try {			
			part.setText(SAMPLE_TEXT);

			InputStream stream = part.getInputStream();
			byte[] dataBytes = new byte[SAMPLE_TEXT.length()];
			stream.read(dataBytes);

			String s = new String(dataBytes, StandardCharsets.UTF_8);
			Assert.assertEquals(SAMPLE_TEXT, s);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentAsStream() {
		try {
			part.setText(SAMPLE_TEXT);

			InputStream stream = part.getContentAsStream();
			byte[] dataBytes = new byte[SAMPLE_TEXT.length()];
			stream.read(dataBytes);

			String s = new String(dataBytes, StandardCharsets.UTF_8);
			Assert.assertEquals(SAMPLE_TEXT, s);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testWrappedObject() {
		try {
			Part javaMailPart = new MimeBodyPart();
			part = new JavaMailPart(javaMailPart);
			Assert.assertTrue("Wrapped object should be same as initialized one",
					part.getWrappedObject() == javaMailPart);//NOSONAR - Indeed referential equality check is needed here
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}