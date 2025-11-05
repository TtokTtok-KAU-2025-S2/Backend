// 소음 현황판 응답
package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NoiseReportResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoiseReportPreviewDto {
        private Long reportId;
        private Integer authorDong;
        private LocalDateTime reportedAt;
        private NoiseCategory category;
        private String summary;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoiseReportListResponse {
        private List<NoiseReportPreviewDto> reports;
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
        public static class NoiseReportDetailDto {
        private Long reportId;
        private Integer authorDong;
        private LocalDateTime reportedAt;
        private NoiseCategory category;
        private String summary; // AI 요약
        private Map<VoteType, Long> voteCounts; // 투표 현황
        private List<CommentResponse.CommentDto> comments; // 댓글 목록
    }
}
