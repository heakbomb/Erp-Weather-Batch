package com.erp.erp_weather_batch.service;

import com.erp.erp_weather_batch.config.KmaProperties;
import com.erp.erp_weather_batch.entity.WeatherHourly;
import com.erp.erp_weather_batch.repository.WeatherHourlyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class WeatherCollectService {

    private final KmaClient kmaClient;
    private final KmaProperties props;
    private final KmaSfctm2Parser parser;
    private final WeatherHourlyRepository repo;

    private static final DateTimeFormatter TM_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * ✅ 특정 시각(targetTm)을 기반으로 1건 수집/저장 (백필에서 사용)
     */
    @Transactional
    public void collectByTm(LocalDateTime targetTm) {
        // tm은 정각 기준으로 맞추기
        LocalDateTime hour = targetTm.withSecond(0).withNano(0).withMinute(0);
        String tm = hour.format(TM_FMT);

        String body = kmaClient.fetchSfctm2(tm);

        WeatherHourly entity = parser.parseOne(body, props.getStn(), 0, 0);
        if (entity == null) {
            System.out.println("[BATCH] no data line found. tm=" + tm);
            return;
        }

        boolean exists = repo.existsByNxAndNyAndBaseDateAndBaseTimeAndForecastDateAndForecastTime(
                entity.getNx(),
                entity.getNy(),
                entity.getBaseDate(),
                entity.getBaseTime(),
                entity.getForecastDate(),
                entity.getForecastTime()
        );

        if (exists) {
            System.out.println("[BATCH] duplicated row skipped. tm=" + tm);
            return;
        }

        repo.save(entity);
        System.out.println("[BATCH] saved weather row. tm=" + tm);
    }

    /**
     * ✅ 현재 시각 기준 1건 수집 (기존 스케줄러에서 사용)
     */
    @Transactional
    public void collectOnceNow() {
        collectByTm(LocalDateTime.now());
    }
}