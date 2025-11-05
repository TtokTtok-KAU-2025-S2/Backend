package TtokTtok.Backend.domain.noise.model;

import lombok.Data; // Lombok 사용 가정

@Data // Getter, Setter, toString 등을 자동 생성
public class NoiseModel {
    // 클라이언트에서 전송되는 폼 데이터와 매칭
    private String category;
    private String description;
    private double avgDb;
    private double maxDb;

    // 최종 분석 텍스트를 담기 위한 필드 (저장 시 사용)
    private String analysisText;
}
