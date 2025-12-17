package com.erp.erp_weather_batch.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "weather_hourly",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_weather_hourly",
            columnNames = {
                "nx", "ny",
                "base_date", "base_time",
                "forecast_date", "forecast_time"
            }
        )
    },
    indexes = {
        // 예보 조회(시간대별) 많이 할 때
        @Index(name = "idx_forecast_dt", columnList = "forecast_date, forecast_time"),
        // 특정 지역 + 예보시각 조회가 많을 때
        @Index(name = "idx_region_forecast", columnList = "nx, ny, forecast_date, forecast_time"),
        // 오래된 데이터 삭제(보관기간 컷)용
        @Index(name = "idx_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherHourly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_id")
    private Long weatherId;

    /** 지역 격자 좌표 (KMA nx/ny) */
    @Column(name = "nx", nullable = false)
    private Integer nx;

    @Column(name = "ny", nullable = false)
    private Integer ny;

    /** 예보 생성 기준(발표) 날짜/시간: base_date + base_time */
    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "base_time", nullable = false)
    private LocalTime baseTime; // HH:mm (예: 02:00, 05:00)

    /** 실제 예보 대상 날짜/시간: fcst_date + fcst_time */
    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "forecast_time", nullable = false)
    private LocalTime forecastTime; // HH:mm (예: 00:00 ~ 23:00)

    /** 기상 요소(필요한 것만 먼저) */
    @Column(name = "temperature")
    private Double temperature; // T1H (°C)

    @Column(name = "humidity")
    private Integer humidity; // REH (%)

    @Column(name = "rainfall_mm")
    private Double rainfallMm; // RN1 (mm) - 파싱 못하면 null or 0 정책

    @Column(name = "sky", length = 4)
    private String sky; // SKY 코드

    @Column(name = "precipitation_type", length = 4)
    private String precipitationType; // PTY 코드

    /** 저장 시각(배치 적재 시각) */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}