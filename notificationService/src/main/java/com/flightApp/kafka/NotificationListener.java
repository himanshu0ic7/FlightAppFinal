package com.flightApp.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.flightApp.dtos.BookingEvent;
import com.flightApp.dtos.NotificationEvent;
import com.flightApp.service.SmsService;

@Component
@Slf4j
public class NotificationListener {

    @Autowired
    private SmsService smsService;

    @KafkaListener(topics = "flight_confirmation", groupId = "notification-group")
    public void handleFlightConfirmation(BookingEvent event) {
        log.info("Received Notification Event for PNR: {}", event.getPnr());

        if (event.getMobileNumber() != null && !event.getMobileNumber().isEmpty()) {
            log.info("Triggering SMS to: {}", event.getMobileNumber());
            smsService.sendSms(event.getMobileNumber(), event.getMessage());
        } else {
            log.warn("No mobile number found for PNR: {}", event.getPnr());
        }

        if (event.getEmail() != null && !event.getEmail().isEmpty()) {
            log.info("Skipping Email to: {} (Email service not yet enabled)", event.getEmail());
           
        }
    }
    
    @KafkaListener(topics = "user_notifications", groupId = "notification-group")
    public void handleUserNotifications(NotificationEvent event) {
        log.info("Received User Notification for mobile Number: ", event.getMobileNumber());

        if (event.getMobileNumber() != null && !event.getMobileNumber().isEmpty()) {
            smsService.sendSms(event.getMobileNumber(), event.getMessage());
        }
    }
}
