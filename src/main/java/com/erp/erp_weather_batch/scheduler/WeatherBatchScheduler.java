package com.erp.erp_weather_batch.scheduler;

import com.erp.erp_weather_batch.service.WeatherCollectService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeatherBatchScheduler {

    private final WeatherCollectService weatherCollectService;
    // WeatherFeatureMaterializeService 의존성 삭제됨

    // ✅ weather.cron 값으로 스케줄 제어 (하루 8번)
    @Scheduled(cron = "${weather.cron}", zone = "Asia/Seoul")
    public void run8TimesPerDay() {
        // 1) 수집 (이건 필수! 원본 저장)
        try {
            weatherCollectService.collectOnceNow();
            System.out.println("[BATCH] weather collected (8x/day)");
        } catch (Exception e) {
            System.out.println("[BATCH] collect failed: " + e.getMessage());
            e.printStackTrace();
        }

        // 2) 피처 테이블 적재 로직은 삭제됨 (Python에서 처리)
    }
}