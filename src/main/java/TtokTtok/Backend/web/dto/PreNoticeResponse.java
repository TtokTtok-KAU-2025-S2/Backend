package TtokTtok.Backend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PreNoticeResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreNoticePreviewDto {
        private Long preNoticeId;
        private Integer authorDong;
        private Integer authorHosu;
        private String title;
        private String eventDate;
        private String eventTime;
        private String eventReason;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreNoticeListResponse {
        private List<PreNoticePreviewDto> preNotices;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreNoticeDetailDto {
        private Long preNoticeId;
        private Integer authorDong;
        private Integer authorHosu;
        private String title;
        private String content;
        private String eventDate;
        private String eventTime;
        private String eventReason;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}