package com.erp.erp_weather_batch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeatherFeatureMaterializeService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * vw_weather_features_with_holiday → weather_features_hourly
     * 최근 N시간만 UPSERT
     */
    @Transactional
    public int upsertRecentHours(int hoursBack) {
        LocalDateTime from = LocalDateTime.now().minusHours(hoursBack);
        Timestamp fromTs = Timestamp.valueOf(from);

        String sql = """
            INSERT INTO weather_features_hourly (
                nx, ny, dt,
                temp_c, humidity_pct, rain_mm, rain_flag,
                sky_code, pty_code,
                hour, dow, is_weekend, month,
                temp_lag_1h, temp_lag_2h, rain_lag_1h,
                temp_avg_24h, rain_sum_24h,
                is_holiday, holiday_name
            )
            SELECT
                nx, ny, dt,
                temp_c, humidity_pct, rain_mm, rain_flag,
                sky_code, pty_code,
                hour, dow, is_weekend, month,
                temp_lag_1h, temp_lag_2h, rain_lag_1h,
                temp_avg_24h, rain_sum_24h,
                is_holiday, holiday_name
            FROM vw_weather_features_with_holiday
            WHERE dt >= ?
            ON DUPLICATE KEY UPDATE
                temp_c = VALUES(temp_c),
                humidity_pct = VALUES(humidity_pct),
                rain_mm = VALUES(rain_mm),
                rain_flag = VALUES(rain_flag),
                sky_code = VALUES(sky_code),
                pty_code = VALUES(pty_code),
                hour = VALUES(hour),
                dow = VALUES(dow),
                is_weekend = VALUES(is_weekend),
                month = VALUES(month),
                temp_lag_1h = VALUES(temp_lag_1h),
                temp_lag_2h = VALUES(temp_lag_2h),
                rain_lag_1h = VALUES(rain_lag_1h),
                temp_avg_24h = VALUES(temp_avg_24h),
                rain_sum_24h = VALUES(rain_sum_24h),
                is_holiday = VALUES(is_holiday),
                holiday_name = VALUES(holiday_name),
                updated_at = CURRENT_TIMESTAMP
            """;

        return jdbcTemplate.update(sql, fromTs);
    }
}