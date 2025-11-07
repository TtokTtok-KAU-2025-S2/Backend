package TtokTtok.Backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentDto {
        private Long commentId;
        private Integer authorDong; // 익명성을 위해 동 정보만 포함
        private String content;
        private LocalDateTime createdAt;
        private Boolean isMyComment; // 본인 댓글인지 여부 (수정/삭제 UI 표시에 사용)
    }
}