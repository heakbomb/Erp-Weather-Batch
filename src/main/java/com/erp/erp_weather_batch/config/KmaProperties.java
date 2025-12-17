package com.erp.erp_weather_batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kma")
public class KmaProperties {
    private String baseUrl;
    private String authKey;
    private int stn;
    private int help = 0;
}