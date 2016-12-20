package com.codenotfound.kafka;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class KafkaSender {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(KafkaSender.class);

	@Autowired
	private KafkaTemplate<Integer, String> kafkaTemplate;

	public void sendMessageAsync(String topic, final String message) {

		// the KafkaTemplate provides asynchronous send methods returning a
		// Future
		ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate
				.send(topic, message);

		// you can register a callback with the listener to receive the result
		// of the send asynchronously
		future.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {

			@Override
			public void onSuccess(SendResult<Integer, String> result) {
				LOGGER.info("sent message='{}' with offset={}", message, result
						.getRecordMetadata().offset());
			}

			@Override
			public void onFailure(Throwable ex) {
				LOGGER.error("unable to send message='{}'", message, ex);
			}
		});

		// alternatively, to block the sending thread, to await the result,
		// invoke the future’s get() method
	}

	public void sendMessageSync(String topic, final String message) {

		long tid = Thread.currentThread().getId();
		
		ListenableFuture<SendResult<Integer, String>> future = kafkaTemplate.send(topic, message);

		SendResult<Integer, String> result = null;

		try {
			result = future.get(10, TimeUnit.SECONDS);
			LOGGER.info(tid + "::Sent message='{}' with offset={}", message, result.getRecordMetadata().offset());
		} catch (TimeoutException e) {
			LOGGER.error("timeout, unable to send message='{}'", message, e);
		} catch (Exception e) {
			LOGGER.error("Unable to send message='{}'", message, e);
		}

	}

}