package TtokTtok.Backend.web.dto;
//서버에서 클라이언트로 공지사항 정보를 보낼 때 사용
import TtokTtok.Backend.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class NoticeResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticePreviewDto {
        private Long noticeId;
        private String title;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeListResponse {
        private List<NoticePreviewDto> notices;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeDetailDto {
        private Long noticeId;
        private String authorName;
        private String title;
        private String content;
        private String imageUrl;
        private LocalDateTime createdAt;
    }
}
