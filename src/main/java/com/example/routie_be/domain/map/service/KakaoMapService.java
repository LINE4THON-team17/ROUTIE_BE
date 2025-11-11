package com.example.routie_be.domain.map.service;

import com.example.routie_be.domain.route.dto.PlaceSelectionDto; // 💡 사용할 DTO 미리 임포트
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 파싱을 위해 필요
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KakaoMapService {

    // .env 파일에서 참조하도록 설정
    @Value("${KAKAO_REST_API_KEY}")
    private String kakaoRestApiKey;

    private final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 주입

    /**
     * 카카오 API를 통해 키워드로 장소를 검색하고, 결과를 PlaceSelectionDto 리스트로 변환합니다.
     */
    public List<PlaceSelectionDto> searchPlaceByKeyword(String keyword) {
        // 검색어 인코딩
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String uri = KAKAO_API_URL + "?query=" + encodedKeyword;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String jsonBody = response.getBody();
            if (jsonBody == null) return Collections.emptyList();

            // 💡 JSON 파싱: 문서(documents) 배열만 추출
            // 실제 구현에서는 KakaoPlaceResponse DTO를 만들고 매핑해야 하지만,
            // 여기서는 List<Map> 형태로 임시 파싱합니다.

            // ObjectMapper를 사용하여 JSON body에서 "documents" 배열을 Map 리스트로 직접 추출
            List<java.util.Map<String, Object>> documents = objectMapper.readValue(
                    jsonBody,
                    new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {}
            ).get("documents");

            if (documents == null) return Collections.emptyList();

            // 추출된 데이터를 PlaceSelectionDto로 변환
            return documents.stream().map(doc -> PlaceSelectionDto.builder()
                    .name((String) doc.get("place_name"))
                    .address((String) doc.get("address_name")) // 정확한 주소
                    .latitude(Double.parseDouble((String) doc.get("y")))
                    .longitude(Double.parseDouble((String) doc.get("x")))
                    .category((String) doc.get("category_group_name")) // 카카오 제공 카테고리
                    .build()
            ).collect(Collectors.toList());

        } catch (Exception e) {
            // 외부 API 호출 실패 시 처리
            throw new RuntimeException("카카오 지도 API 호출 중 오류 발생: " + e.getMessage());
        }
    }
}