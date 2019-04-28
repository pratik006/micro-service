package com.pega.platform.integrationcore;

import com.pega.platform.integrationcore.client.email.*;
import com.pega.platform.integrationcore.client.email.config.EmailClientConfiguration;
import com.pega.platform.integrationcore.client.email.config.EmailReceiverProfile;
import com.pega.platform.integrationcore.client.email.config.EmailSenderProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.ContentType;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/email")
public class EmailService {
    private static Logger LOG = LoggerFactory.getLogger(EmailService.class);

    private EmailClient emailClient;

    @Autowired
    EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }

    @GetMapping
    public String test() {
        return "hashcode: "+EmailService.class+" "+this.hashCode();
    }

    @PostMapping("/send")
    public EmailClientResponse sendEmail(@RequestBody SerializableMimeMessage sMessage, boolean autoDisconnect) throws EmailClientException {
        emailClient.initialize(getConfig());
        MIMEMessage message = emailClient.createMessage(null);
        message.setSubject(sMessage.getSubject());
        message.setRecipient(MIMEMessage.RecipientType.TO, sMessage.getTo());
        message.setFrom(sMessage.getFrom());

        MIMEMultipart multiPart = emailClient.createMultipart("mixed");
        MIMEPart part = emailClient.createPart();
        part.setText(sMessage.getContent());
        part.setHeader("Content-type", "text/plain");
        multiPart.addBodyPart(part);

        message.setContent(multiPart);
        LOG.debug("sendEmail: subject: "+message.getSubject()+"\tTo: "+message.getAllRecipients().stream().collect(Collectors.joining(";")));
        return emailClient.sendEmail(message, autoDisconnect);
        //return null;
    }

    private EmailClientConfiguration getConfig() {
        EmailSenderProfile senderProfile = new EmailSenderProfile();
        senderProfile.setHost("smtp.gmail.com");
        senderProfile.setPort(465);
        senderProfile.setUserId("vickysengupta006@gmail.com");
        senderProfile.setUserPassword("chinat0wn");
        senderProfile.setProtocol("smtp");
        senderProfile.setUseSSL(true);

        EmailReceiverProfile receiverProfile = new EmailReceiverProfile();
        receiverProfile.setProtocol("imap");
        receiverProfile.setHost("imap.mail.com");
        receiverProfile.setPort(999);
        receiverProfile.setUserId("vickysengupta006@gmail.com");
        receiverProfile.setUserPassword("chinat0wn");
        receiverProfile.setUseSSL(true);
        receiverProfile.setFolderName("INBOX");

        EmailClientConfiguration config = new EmailClientConfiguration();
        config.setSenderProfile(senderProfile);
        config.setReceiverProfile(receiverProfile);

       return config;
    }

    @PostMapping("/receive")
    public List<MIMEMessage> retrieveEmails(boolean onlyUnread, boolean autoDisconnect) throws EmailClientException {
        LOG.debug("retrieveEmails: onlyUnread -> "+ onlyUnread + "\tautoDisconnect -> "+autoDisconnect);
        return emailClient.retrieveEmails(onlyUnread, autoDisconnect);
    }
}
