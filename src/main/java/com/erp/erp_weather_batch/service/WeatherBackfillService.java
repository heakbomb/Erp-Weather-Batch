package com.erp.erp_weather_batch.service;

import com.erp.erp_weather_batch.config.BackfillProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherBackfillService {

    private final WeatherCollectService collectService;
    private final BackfillProperties backfillProps;

    public void runBackfill() {
        LocalDateTime from = backfillProps.getFrom();
        LocalDateTime to = backfillProps.getTo();
        long sleepMs = backfillProps.getSleepMs();

        if (from == null || to == null) {
            throw new IllegalArgumentException("backfill.from / backfill.to 를 설정해야 합니다.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("backfill.from 은 backfill.to 보다 이후일 수 없습니다.");
        }

        // ✅ 시작을 정각으로 보정 (00분 00초)
        LocalDateTime cur = from.withMinute(0).withSecond(0).withNano(0);

        System.out.println("[BACKFILL] start: " + cur + " ~ " + to);

        long total = 0;
        long ok = 0;

        while (!cur.isAfter(to)) {
            total++;
            try {
                collectService.collectByTm(cur); // ✅ 이게 15개 지역을 한 번에 수집
                ok++;
            } catch (Exception e) {
                System.out.println("[BACKFILL] error at " + cur + " : " + e.getMessage());
            }

            if (sleepMs > 0) {
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException ignored) {
                }
            }

            // ✅ 3시간 단위(하루 8번)
            cur = cur.plusHours(3);
        }

        System.out.println("[BACKFILL] done. total=" + total + ", processed=" + ok);
    }
}