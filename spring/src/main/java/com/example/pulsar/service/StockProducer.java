package com.example.pulsar.service;

import com.example.pulsar.dto.StockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.pulsar.core.PulsarTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockProducer {
    
    private final PulsarTemplate<StockData> pulsarTemplate;
    private final StockDataGenerator stockDataGenerator;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    @Value("${spring.pulsar.producer.topic-name}")
    private String topicName;
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void publishStockData() {
        for (String ticker : stockDataGenerator.getTickerList()) {
            executorService.submit(() -> {
                try {
                    final StockData stockData = stockDataGenerator.generateStockData(ticker);
                    pulsarTemplate.send(topicName, stockData);
                    log.info("Published data for ticker: {}", stockData);
                } catch (Exception e) {
                    log.error("Error publishing data for ticker {}: {}", ticker, e.getMessage(), e);
                }
            });
        }
    }
} 