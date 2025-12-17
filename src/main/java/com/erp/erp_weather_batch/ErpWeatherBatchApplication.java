package com.erp.erp_weather_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class ErpWeatherBatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpWeatherBatchApplication.class, args);
    }
}