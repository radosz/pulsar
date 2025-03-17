package com.example.pulsar.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class StockData {
    private String ticker;
    private String high;
    private String low;
    private String close;
    private long volume;
    private long timestamp;
} 