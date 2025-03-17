package com.example.pulsar.service;

import com.example.pulsar.dto.StockData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class StockProducer {
    
    private final ObjectMapper objectMapper;
    private final Producer<byte[]> producer;
    private final List<String> tickerList = Arrays.asList(
            "ABT", "ABBV", "ABMD", "ACN", "ATVI", "ADBE", "AMD",
            "AAP", "AES", "AFL", "A", "APD"
    );
    private final Random random = new Random();
    
    public StockProducer(PulsarClient pulsarClient, ObjectMapper objectMapper) throws PulsarClientException {
        this.objectMapper = objectMapper;
        this.producer = pulsarClient.newProducer()
                .topic("stock-topic-partitioned1")
                .producerName("producer1")
                .messageRoutingMode(MessageRoutingMode.RoundRobinPartition)
                .create();
    }
    
    private StockData generateStockData(String ticker) {
        // Generate base price around 100-150
        double basePrice = 100 + random.nextDouble() * 50;
        
        // Generate high, low, close prices around the base price
        double high = basePrice + (random.nextDouble() * 5);
        double low = basePrice - (random.nextDouble() * 5);
        double close = low + (random.nextDouble() * (high - low));
        
        // Generate volume between 100k and 1M
        long volume = 100000 + random.nextInt(900000);
        
        return StockData.builder()
                .ticker(ticker)
                .high(String.format("%.8f", high))
                .low(String.format("%.8f", low))
                .close(String.format("%.8f", close))
                .volume(volume)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void publishStockData() throws PulsarClientException {
        for (String ticker : tickerList) {
            try {
                StockData stockData = generateStockData(ticker);
                String jsonData = objectMapper.writeValueAsString(stockData);
                producer.send(jsonData.getBytes(StandardCharsets.UTF_8));
                log.info("Published data for ticker: {} - High: {}, Low: {}, Close: {}, Volume: {}", 
                    stockData.getTicker(), 
                    stockData.getHigh(), 
                    stockData.getLow(), 
                    stockData.getClose(), 
                    stockData.getVolume());
            } catch (Exception e) {
                log.error("Error publishing data for ticker {}: {}", ticker, e.getMessage());
            }
        }
    }
} 