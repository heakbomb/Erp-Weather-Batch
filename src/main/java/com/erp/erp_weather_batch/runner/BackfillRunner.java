package com.erp.erp_weather_batch.runner;

import com.erp.erp_weather_batch.config.BackfillProperties;
import com.erp.erp_weather_batch.service.WeatherBackfillService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackfillRunner implements CommandLineRunner {

    private final BackfillProperties props;
    private final WeatherBackfillService backfillService;

    @Override
    public void run(String... args) {
        // ✅ 운영에서는 backfill.enabled=false 로 두면 절대 실행 안 됨
        if (!props.isEnabled()) {
            System.out.println("[BACKFILL] disabled.");
            return;
        }

        System.out.println("[BACKFILL] enabled. running backfill...");
        backfillService.runBackfill();
        System.out.println("[BACKFILL] done.");
    }
}