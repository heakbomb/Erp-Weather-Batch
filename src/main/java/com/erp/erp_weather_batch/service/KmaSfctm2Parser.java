package com.erp.erp_weather_batch.service;

import com.erp.erp_weather_batch.entity.WeatherHourly;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class KmaSfctm2Parser {

    private static final DateTimeFormatter TM_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /**
     * kma_sfctm2 응답에서 "실제 데이터 1줄"을 찾아 WeatherHourly로 변환
     */
    public WeatherHourly parseOne(String body, int stn, int nx, int ny) {
        if (body == null || body.isBlank()) return null;

        String[] lines = body.split("\\R"); // 줄 단위 split
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("#")) continue; // 헤더/설명 제거

            // 데이터 라인: 공백 기준 컬럼들로 구성
            String[] c = line.split("\\s+");
            // 최소 길이 검증 (데이터가 짧게 오면 제외)
            if (c.length < 16) continue;

            // c[0]=TM, c[1]=STN
            if (!c[1].equals(String.valueOf(stn))) continue;

            // 필요한 필드 파싱
            String tm = c[0];
            LocalDateTime observedAt = LocalDateTime.parse(tm, TM_FMT);

            Double windSpeed = toDoubleOrNull(c[3]);  // WS
            Double temp = toDoubleOrNull(c[11]);      // TA
            Double humidityD = toDoubleOrNull(c[13]); // HM(실수로 오기도 함)
            Integer humidity = humidityD == null ? null : (int)Math.round(humidityD);

            Double rainfallMm = toDoubleOrNull(c[15]); // RN (없으면 -9)

            // -9 같은 결측치 처리 (KMA typ01에서 자주 씀)
            windSpeed = normalizeMissing(windSpeed);
            temp = normalizeMissing(temp);
            rainfallMm = normalizeMissing(rainfallMm);
            if (humidity != null && humidity <= -9) humidity = null;

            // ✅ 너희 엔티티 스키마(현재 테이블)에 맞춰 세팅
            return WeatherHourly.builder()
                    .baseDate(observedAt.toLocalDate())
                    .baseTime(observedAt.toLocalTime())
                    .forecastDate(observedAt.toLocalDate())
                    .forecastTime(observedAt.toLocalTime())
                    .temperature(temp)
                    .humidity(humidity)
                    .rainfallMm(rainfallMm)
                    .nx(nx)
                    .ny(ny)
                    .precipitationType(null) // sfctm2는 PTY가 예보쪽에 가까워서 우선 null
                    .sky(null)
                    .build();
        }

        return null;
    }

    private Double toDoubleOrNull(String s) {
        try { return Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }

    private Double normalizeMissing(Double v) {
        if (v == null) return null;
        if (v <= -9) return null; // -9, -9.0 등 결측 처리
        return v;
    }
}