package com.swp.carcare.service;

import java.util.concurrent.CompletableFuture;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String message);
}
