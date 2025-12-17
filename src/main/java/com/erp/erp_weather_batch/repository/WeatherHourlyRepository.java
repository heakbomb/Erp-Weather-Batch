package com.erp.erp_weather_batch.repository;

import com.erp.erp_weather_batch.entity.WeatherHourly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface WeatherHourlyRepository extends JpaRepository<WeatherHourly, Long> {

    boolean existsByNxAndNyAndBaseDateAndBaseTimeAndForecastDateAndForecastTime(
            int nx,
            int ny,
            LocalDate baseDate,
            LocalTime baseTime,
            LocalDate forecastDate,
            LocalTime forecastTime
    );
}