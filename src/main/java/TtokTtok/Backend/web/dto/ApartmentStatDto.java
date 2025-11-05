package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.ActivationStatus;
import TtokTtok.Backend.domain.ApartmentStat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class ApartmentStatDto {

    /**
     * 전국 아파트 소음 통계 API의 최종 응답 DTO
     */
    @Getter
    @Builder
    public static class NationwideStatResponse {
        private List<ApartmentStatDetail> apartments;
        private Integer totalApartments; // (추가) 총 아파트 수
    }

    /**
     * 개별 아파트의 통계 DTO
     * (이 DTO가 파이 차트의 데이터가 됩니다)
     */
    @Getter
    @Builder
    public static class ApartmentStatDetail {
        private Long apartmentId;
        private String apartmentName;
        private ActivationStatus status;
        private Map<String, Long> noiseDistribution; // (JSON을 Map으로 변환)

        // Entity -> DTO 변환기
        public static ApartmentStatDetail fromEntity(ApartmentStat stat, ObjectMapper objectMapper) {
            Map<String, Long> noiseMap = parseJsonToMap(stat.getMainNoiseTypesJson(), objectMapper);

            return ApartmentStatDetail.builder()
                    .apartmentId(stat.getApartment().getId())
                    .apartmentName(stat.getApartment().getName())
                    .status(stat.getActivationStatus())
                    .noiseDistribution(noiseMap)
                    .build();
        }

        // JSON 문자열을 Map<String, Long>으로 변환
        private static Map<String, Long> parseJsonToMap(String json, ObjectMapper objectMapper) {
            if (json == null || json.isEmpty()) {
                return Map.of();
            }
            try {
                return objectMapper.readValue(json, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                log.warn("ApartmentStat JSON 파싱 실패: {}", json, e);
                return Map.of();
            }
        }
    }
}