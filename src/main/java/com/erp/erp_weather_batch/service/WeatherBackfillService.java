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
        int step = backfillProps.getStepMinutes();
        long sleepMs = backfillProps.getSleepMs();

        if (from == null || to == null) {
            throw new IllegalArgumentException("backfill.from / backfill.to 를 설정해야 합니다.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("backfill.from 은 backfill.to 보다 이후일 수 없습니다.");
        }

        System.out.println("[BACKFILL] start: " + from + " ~ " + to);

        LocalDateTime cur = from;
        long total = 0;
        long ok = 0;

        while (!cur.isAfter(to)) {
            total++;
            try {
                collectService.collectByTm(cur);
                ok++;
            } catch (Exception e) {
                // 백필은 "죽지 말고 다음 시간으로"가 중요
                System.out.println("[BACKFILL] error at " + cur + " : " + e.getMessage());
            }

            // rate limit 대비
            if (sleepMs > 0) {
                try { Thread.sleep(sleepMs); } catch (InterruptedException ignored) {}
            }

            cur = cur.plusMinutes(step);
        }

        System.out.println("[BACKFILL] done. total=" + total + ", processed=" + ok);
    }
}