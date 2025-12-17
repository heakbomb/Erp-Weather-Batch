package com.erp.erp_weather_batch.service;

import com.erp.erp_weather_batch.entity.WeatherHourly;
import com.erp.erp_weather_batch.repository.WeatherHourlyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WeatherHourlyService {

    private final WeatherHourlyRepository weatherHourlyRepository;

    @Transactional
    public void saveDummyOneRow() {
        WeatherHourly w = WeatherHourly.builder()
                // ✅ 필수값들(엔티티 nullable=false) 채워줘야 저장됨
                .nx(60) // 예시: 서울 격자값(너희 지역에 맞게 바꿔)
                .ny(127)
                .baseDate(LocalDate.now())
                .baseTime(LocalTime.now().withSecond(0).withNano(0)) // ✅ LocalTime
                .forecastDate(LocalDate.now())
                .forecastTime(LocalTime.of(15, 0)) // 예시 15:00

                // 옵션값
                .temperature(10.5)
                .humidity(55)
                .rainfallMm(0.0)
                .sky("1")
                .precipitationType("0")

                // createdAt은 @PrePersist로 자동세팅 가능하지만, 넣어도 OK
                .createdAt(LocalDateTime.now())
                .build();

        weatherHourlyRepository.save(w);
    }
}