package com.pega.platform.integrationcore.client.email.internal.javamail.stubs;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * {@link Transport} stub to deliver to {@link MailboxStub}.
 */
public class TransportStub extends Transport {
	
	private String user;
	
	private static boolean enableAuthRetry = false;
	
	public static void enableAuthRetry(boolean authRetry) {
		enableAuthRetry = authRetry;
	}
	
    public TransportStub(Session session, URLName urlname) {
        super(session, urlname);
    }

    public void connect(String host, int port, String user, String password) throws MessagingException {
        System.out.println("Connecting to TransportStub using Host:" + host + "  Port:" + port
        		+ "  User:" + user + "  Password:" + password);
        if(enableAuthRetry && user!=null && password!=null && port==25) {
        	throw new AuthenticationFailedException("no authentication mechanisms supported");
        }
        this.user = user;
    }
    
    public void connect(String host, String user, String password) throws MessagingException {
        System.out.println("Connecting to TransportStub using Host:" + host 
        		+ "  User:" + user + "  Password:" + password);
        if(enableAuthRetry && user!=null && password!=null) {
        	throw new AuthenticationFailedException("no authentication mechanisms supported");
        }
        this.user = user;
    }

    public void sendMessage(Message msg, Address[] addresses) throws MessagingException {
    	if(msg.getFrom() == null) {
    		msg.setFrom(new InternetAddress(user));
    	}
        for (Address a : addresses) {
            // create a copy to isolate the sender and the receiver
            MailboxStub mailbox = MailboxStub.get(a);
            if(mailbox.isError()){
                throw new MessagingException("Simulated error sending message to "+a);
            }
            System.out.println("Sending message to:" + a.toString());
            mailbox.add(new MimeMessage((MimeMessage)msg));
            //mailbox.add((MimeMessage)msg);
        }
    }
}
