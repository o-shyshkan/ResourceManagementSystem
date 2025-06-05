package com.test;

import com.test.event.ResourceEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class NotificationApplication {
	public static final Logger logger = LogManager.getLogger(NotificationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(ResourceEvent resourceEvent) {
		// send out on email notification
		logger.info("Received notification from ResourceService - {}", resourceEvent.getResource());
	}

	@KafkaListener(topics = "notificationTopicAllData")
	public void handleAllDataNotification(ResourceEvent resourceEvent) {
		// send out on email notification
		logger.info("Received AllData notification from ResourceService - {}", resourceEvent.getResource());
	}
}
