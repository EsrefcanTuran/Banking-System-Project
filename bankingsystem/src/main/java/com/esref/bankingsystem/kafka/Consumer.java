package com.esref.bankingsystem.kafka;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

	@KafkaListener(topics = "logs", groupId = "logs_consumer_group")
	public void listenLogs(@Payload String message) {
		
	    try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("logs.txt", true));
			bw.write(message + "\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}