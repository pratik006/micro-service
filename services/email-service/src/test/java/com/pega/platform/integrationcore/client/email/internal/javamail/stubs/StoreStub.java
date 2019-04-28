package com.pega.platform.integrationcore.client.email.internal.javamail.stubs;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

/**
 * {@link Store} stub backed by {@link MailboxStub}.
 */
public class StoreStub extends Store {
    private FolderStub folder;
    private String address;

    public StoreStub(Session session, URLName urlname) {
        super(session, urlname);
    }

    public void connect() throws MessagingException {
    	System.out.println("Connecting to StoreStub using Host:" + url.getHost() + "  Port:" + url.getPort()
    			+ "  User:" + url.getUsername() + "  Password:" + url.getPassword());
        connect(url.getHost(), url.getPort(), url.getUsername(), url.getPassword());
    }

    protected boolean protocolConnect(String host, int port, String user, String password) throws MessagingException {
        address = user;
        MailboxStub mailbox = MailboxStub.get(address);
        folder = new FolderStub(this, mailbox);
        if(mailbox.isError()) {
            throw new MessagingException("Simulated error connecting to "+address);
        }
        return true;
    }

    public Folder getDefaultFolder() throws MessagingException {
        return folder;
    }

    public Folder getFolder(String name) throws MessagingException {
        return folder;
    }

    public Folder getFolder(URLName url) throws MessagingException {
        return folder;
    }
}
