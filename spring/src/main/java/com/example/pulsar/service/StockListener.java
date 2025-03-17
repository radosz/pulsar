package com.example.pulsar.service;

import com.example.pulsar.dto.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockListener {
    
    @PulsarListener(
            subscriptionName = "${spring.pulsar.consumer.subscription.name}",
            topics = "${spring.pulsar.consumer.topics}"
    )
    public void consumeMessage(StockData stockData) {
        log.info("Received stock data: {}", stockData);
    }
} 