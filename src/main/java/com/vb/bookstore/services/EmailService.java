package com.vb.bookstore.services;

public interface EmailService {
    void sendSimpleMail(String recipient, String msgBody, String subject);
}