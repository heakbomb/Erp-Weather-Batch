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

    public String fetchSfctm2(String tm) {
        String url = UriComponentsBuilder
                .fromUriString(props.getBaseUrl())
                .queryParam("tm", tm)
                .queryParam("stn", props.getStn())
                .queryParam("help", props.getHelp())
                .queryParam("authKey", props.getAuthKey())
                .build(true)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}