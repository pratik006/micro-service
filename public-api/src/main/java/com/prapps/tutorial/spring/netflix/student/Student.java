package com.prapps.tutorial.spring.netflix.student;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> messages;

    public Student() { }

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }

        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
