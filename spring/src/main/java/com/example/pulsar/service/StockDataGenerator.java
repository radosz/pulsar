package com.example.pulsar.service;

import com.example.pulsar.dto.StockData;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class StockDataGenerator {
    @Getter
    private final List<String> tickerList = Arrays.asList(
            "ABT", "ABBV", "ABMD", "ACN", "ATVI", "ADBE", "AMD",
            "AAP", "AES", "AFL", "A", "APD"
    );
    private final Random random = new Random();
    
    public StockData generateStockData(String ticker) {
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
} 