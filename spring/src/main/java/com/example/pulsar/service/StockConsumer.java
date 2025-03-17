package com.example.pulsar.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockConsumer {

    private final PulsarClient pulsarClient;
    private Consumer<byte[]> consumer;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    
    @PostConstruct
    public void init() throws PulsarClientException {
        consumer = pulsarClient.newConsumer()
                .topic("stock-topic-partitioned1")
                .subscriptionName("training-subscription-partitioned1")
                .subscriptionType(SubscriptionType.Key_Shared)
                .consumerName("consumer1")
                .subscribe();

        // Start consuming messages using ExecutorService
        executorService.submit(this::consumeMessages);
        log.info("Stock consumer started");
    }

    private void consumeMessages() {
        while (isRunning.get()) {
            try {
                Message<byte[]> msg = consumer.receive();
                String messageData = new String(msg.getData());
                log.info("Received message: {} with ID: {}", messageData, msg.getMessageId());
                consumer.acknowledge(msg);
            } catch (PulsarClientException e) {
                if (isRunning.get()) {
                    log.error("Error consuming message: {}", e.getMessage());
                } else {
                    log.info("Consumer shutting down");
                    break;
                }
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            log.info("Shutting down stock consumer...");
            isRunning.set(false);
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (consumer != null) {
                consumer.close();
            }
            log.info("Stock consumer shutdown completed");
        } catch (Exception e) {
            log.error("Error during shutdown", e);
            executorService.shutdownNow();
        }
    }
} 