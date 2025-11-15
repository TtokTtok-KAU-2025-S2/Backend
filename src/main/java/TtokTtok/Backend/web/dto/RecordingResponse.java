package TtokTtok.Backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}