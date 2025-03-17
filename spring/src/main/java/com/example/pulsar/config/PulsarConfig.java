package com.example.pulsar.config;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class PulsarConfig {
    
    @Value("${pulsar.service-url}")
    private String pulsarServiceUrl;
    
    @Value("${pulsar.thread-pool.io-threads:1}")
    private int ioThreads;
    
    @Value("${pulsar.thread-pool.listener-threads:1}")
    private int listenerThreads;
    
    @Bean
    public PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(pulsarServiceUrl)
                .operationTimeout(30, TimeUnit.SECONDS)
                .ioThreads(ioThreads)
                .connectionsPerBroker(1)
                .listenerThreads(listenerThreads)
                .maxLookupRequests(50000)
                .build();
    }
} 