package TtokTtok.Backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature; // 추가
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // 추가
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    /**
     * GPT 모델의 JSON 응답 및 API 응답 변환을 위해 필요합니다.
     * Java 8 날짜/시간(LocalDateTime) 처리를 위해 JavaTimeModule을 등록합니다.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. 핵심: LocalDateTime 처리를 위한 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());

        // 2. 선택: 날짜를 배열([2024,1,1]) 대신 문자열("2024-01-01")로 직렬화
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }
}