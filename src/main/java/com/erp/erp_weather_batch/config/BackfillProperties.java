package com.erp.erp_weather_batch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "backfill")
public class BackfillProperties {
    private boolean enabled;
    private LocalDateTime from;
    private LocalDateTime to;
    private int stepMinutes = 60;
    private long sleepMs = 300;
}