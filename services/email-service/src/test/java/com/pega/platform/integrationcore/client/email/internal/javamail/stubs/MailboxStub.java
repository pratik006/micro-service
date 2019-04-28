package com.pega.platform.integrationcore.client.email.internal.javamail.stubs;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory mailbox stub that hosts messages.
 */
public class MailboxStub extends ArrayList<Message> {

	private static final long serialVersionUID = 1L;

	private final Address address;
    private boolean error;
    
    /**
     * All mailboxes.
     */
    private static final Map<Address,MailboxStub> mailboxes = new HashMap<Address,MailboxStub>();

    public MailboxStub(Address address) {
        this.address = address;
    }

    /**
     * Gets the e-mail address of this mailbox.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Returns true if this mailbox is flagged as 'error'.
     *
     * @see #setError(boolean)
     */
    public boolean isError() {
        return error;
    }

    /**
     * Sets if this mailbox should be flagged as 'error'.
     *
     * Any sending/receiving operation with an error mailbox
     * will fail. This behavior can be used to test the error
     * handling behavior of the application.
     */
    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Get the inbox for the given address.
     */
    public static synchronized MailboxStub get(Address a) {
        MailboxStub inbox = mailboxes.get(a);
        if(inbox==null) {
        	inbox=new MailboxStub(a);
            mailboxes.put(a, inbox);
        }
        return inbox;
    }

    public static MailboxStub get(String address) throws AddressException {
        return get(new InternetAddress(address));
    }

    /**
     * Discards all the mailboxes and its data.
     */
    public static void clearAll() {
    	System.out.println("Clearing all Mailboxes");
        mailboxes.clear();
    }
}
