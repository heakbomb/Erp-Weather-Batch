package com.erp.erp_weather_batch.service;

import com.erp.erp_weather_batch.config.KmaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KmaClient {

    private final RestTemplate restTemplate;
    private final KmaProperties props;

    // ✅ 기존 유지 (단일 stn)
    public String fetchSfctm2(String tm) {
        return fetchSfctm2(tm, props.getStn());
    }

    // ✅ 추가: stn을 호출마다 바꿀 수 있게
    public String fetchSfctm2(String tm, int stn) {
        String url = UriComponentsBuilder
                .fromUriString(props.getBaseUrl())
                .queryParam("tm", tm)
                .queryParam("stn", stn)
                .queryParam("help", props.getHelp())
                .queryParam("authKey", props.getAuthKey())
                .build(true)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}