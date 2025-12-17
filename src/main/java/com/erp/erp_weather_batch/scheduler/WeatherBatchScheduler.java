package com.erp.erp_weather_batch.scheduler;

import com.erp.erp_weather_batch.service.WeatherCollectService;
import com.erp.erp_weather_batch.service.WeatherFeatureMaterializeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherBatchScheduler {

    private final WeatherCollectService weatherCollectService;
    private final WeatherFeatureMaterializeService materializeService;

    // ✅ (프로퍼티로 뺌) weather.cron 값으로 스케줄 제어
    @Scheduled(cron = "${weather.cron}", zone = "Asia/Seoul")
    public void run8TimesPerDay() {

        boolean collectedOk = false;

        // 1) 수집
        try {
            weatherCollectService.collectOnceNow();
            collectedOk = true;
            System.out.println("[BATCH] weather collected (8x/day)");
        } catch (Exception e) {
            System.out.println("[BATCH] collect failed: " + e.getMessage());
            e.printStackTrace();
        }

        // 2) 피처 테이블 적재 (수집 성공했을 때만 실행 추천)
        if (!collectedOk) {
            System.out.println("[BATCH] skip feature upsert because collect failed.");
            return;
        }

        try {
            int affected = materializeService.upsertRecentHours(48);
            System.out.println("[BATCH] features upsert affected=" + affected);
        } catch (Exception e) {
            System.out.println("[BATCH] features upsert failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}