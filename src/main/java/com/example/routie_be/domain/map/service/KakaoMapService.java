package com.example.routie_be.domain.map.service;

import com.example.routie_be.domain.route.dto.PlaceSelectionDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoMapService {
    @Value("${KAKAO_REST_API_KEY:default_rest_key}")
    private String kakaoRestApiKey;

    private final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    public List<PlaceSelectionDto> searchPlaceByKeyword(String keyword) {
        System.out.println("DEBUG KAKAO KEY: [" + kakaoRestApiKey + "]");

        String uri = KAKAO_API_URL + "?query=" + keyword;

        System.out.println("DEBUG URI: " + uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            String jsonBody = response.getBody();
            if (jsonBody == null) return Collections.emptyList();

            System.out.println("Kakao API Raw Response: " + jsonBody);

            Map<String, Object> responseMap =
                    objectMapper.readValue(jsonBody, new TypeReference<Map<String, Object>>() {});

            List<Map<String, Object>> documents =
                    (List<Map<String, Object>>) responseMap.get("documents");

            if (documents == null || documents.isEmpty()) return Collections.emptyList();

            return documents.stream()
                    .map(
                            doc ->
                                    PlaceSelectionDto.builder()
                                            .name((String) doc.get("place_name"))
                                            .address((String) doc.get("address_name"))
                                            .latitude(Double.parseDouble((String) doc.get("y")))
                                            .longitude(Double.parseDouble((String) doc.get("x")))
                                            .category((String) doc.get("category_group_name"))
                                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("카카오 지도 API 호출 및 파싱 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
