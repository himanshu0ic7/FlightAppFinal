package com.flightApp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (accountSid != null && !accountSid.contains("xxx")) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully.");
        } else {
            log.warn("Twilio credentials not found. SMS will be logged to console only.");
        }
    }

    public void sendSms(String to, String messageBody) {
    	log.info("------------------------------------------------");
        log.info("SENDING SMS TO: {}", to);
        log.info("BODY: {}", messageBody);
        log.info("------------------------------------------------");

        try {
            if (!to.startsWith("+")) {
                to = "+91" + to;
                log.info("Added default country code. New number: {}", to);
            }

            if (accountSid != null && !accountSid.contains("xxx")) {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(to),
                        new com.twilio.type.PhoneNumber(fromNumber),
                        messageBody
                ).create();
                
                log.info("SMS sent via Twilio! SID: {}", message.getSid());
            } else {
                log.warn("Twilio Credentials are invalid/dummy. Skipping actual call.");
            }
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage());
        }
    }
}
