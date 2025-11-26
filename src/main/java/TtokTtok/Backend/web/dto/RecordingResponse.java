package TtokTtok.Backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class RecordingResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadDto {
        private Long recordingId; // DB에 저장된 ID
        private String fileUrl;   // S3 업로드 URL
        private String createdAt; // 생성 시간
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordDto {
        private Long recordingId;
        private String fileUrl;
        private String originalFileName;
        private Integer duration;
        private Double dbMax;
        private Double dbAvg;
        private LocalDateTime createdAt;
    }

}