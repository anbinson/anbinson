package com.tsb.ods;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.schema.registry.client.EnableSchemaRegistryClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableSchemaRegistryClient
public class AdsCustomerWriteApplication {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    public static void main(String[] args) {
        SpringApplication.run(AdsCustomerWriteApplication.class);
    }
    
    @PreDestroy
    public void onExit() {
        log.info("Exiting Application : {}", applicationName);
    }
}