package com.erp.erp_weather_batch.service;

import com.erp.erp_weather_batch.config.KmaProperties;
import com.erp.erp_weather_batch.entity.WeatherHourly;
import com.erp.erp_weather_batch.repository.WeatherHourlyRepository;
import com.erp.erp_weather_batch.util.DfsGridConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherCollectService {

    private final KmaClient kmaClient;
    private final KmaProperties props;
    private final KmaSfctm2Parser parser;
    private final WeatherHourlyRepository repo;

    private static final DateTimeFormatter TM_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    // ✅ “수집 대상 15개” (일단 큰 도시 기준)
    // stn은 너가 쓰는 sfctm2 기준 지점번호고,
    // nx/ny는 위경도→DFS 변환으로 자동 계산해서 weather_hourly에 같이 저장됨.
    private static final List<Target> TARGETS = List.of(
            new Target("서울", 108, 37.5665, 126.9780),
            new Target("인천", 112, 37.4563, 126.7052),
            new Target("수원", 119, 37.2636, 127.0286),
            new Target("대전", 133, 36.3504, 127.3845),
            new Target("청주", 131, 36.6424, 127.4890),
            new Target("춘천", 101, 37.8813, 127.7298),
            new Target("강릉", 105, 37.7519, 128.8761),
            new Target("전주", 146, 35.8242, 127.1480),
            new Target("광주", 156, 35.1595, 126.8526),
            new Target("목포", 165, 34.8118, 126.3922),
            new Target("대구", 143, 35.8722, 128.6014),
            new Target("포항", 138, 36.0190, 129.3435),
            new Target("부산", 159, 35.1796, 129.0756),
            new Target("울산", 152, 35.5384, 129.3114),
            new Target("제주", 184, 33.4996, 126.5312)
    );

    /**
     * ✅ 특정 시각(targetTm)을 기반으로 “15개 지역” 수집/저장
     */
    @Transactional
    public void collectByTm(LocalDateTime targetTm) {
        // tm은 정각 기준으로 맞추기
        LocalDateTime hour = targetTm.withSecond(0).withNano(0).withMinute(0);
        String tm = hour.format(TM_FMT);

        for (Target t : TARGETS) {
            try {
                // 1) stn별 호출
                String body = kmaClient.fetchSfctm2(tm, t.stn);

                // 2) nx/ny는 도시 위경도 기준 DFS 변환
                var xy = DfsGridConverter.toXY(t.lat, t.lon);

                WeatherHourly entity = parser.parseOne(body, t.stn, xy.nx(), xy.ny());
                if (entity == null) {
                    System.out.println("[BATCH] no data line. tm=" + tm + " stn=" + t.stn + " (" + t.name + ")");
                    continue;
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
                    System.out.println("[BATCH] duplicated skipped. tm=" + tm + " (" + t.name + ")");
                    continue;
                }

                repo.save(entity);
                System.out.println("[BATCH] saved. tm=" + tm + " stn=" + t.stn + " " + t.name
                        + " nx=" + entity.getNx() + " ny=" + entity.getNy());
            } catch (Exception e) {
                System.out.println("[BATCH] failed. tm=" + tm + " stn=" + t.stn + " (" + t.name + "): " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * ✅ 현재 시각 기준 1회 수집
     */
    @Transactional
    public void collectOnceNow() {
        collectByTm(LocalDateTime.now());
    }

    private static class Target {
        final String name;
        final int stn;
        final double lat;
        final double lon;

        Target(String name, int stn, double lat, double lon) {
            this.name = name;
            this.stn = stn;
            this.lat = lat;
            this.lon = lon;
        }
    }
}