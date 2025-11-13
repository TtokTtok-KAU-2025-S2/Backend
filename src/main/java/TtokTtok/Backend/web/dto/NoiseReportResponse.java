// 소음 현황판 응답
package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.NoiseCategory;
import TtokTtok.Backend.common.enums.VoteType;
import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class NoiseReportResponse {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoiseReportPreviewDto {
        private Long reportId;
        private Integer authorDong;
        private LocalDateTime reportedAt;
        private NoiseCategory category;
        private String summary;
        private Long totalParticipants;
        private Long totalEligibleVoters;
        private Long heardCount;
        private Long notHeardCount;
        private Long beCarefulCount;
        private Long commentCount;

        public NoiseReportPreviewDto(Long reportId, Integer authorDong, LocalDateTime reportedAt,
                                     NoiseCategory category, String summary, Long commentCount,
                                     Long heardCount, Long notHeardCount, Long beCarefulCount) {
            this.reportId = reportId;
            this.authorDong = authorDong;
            this.reportedAt = reportedAt;
            this.category = category;
            this.summary = summary;
            this.commentCount = commentCount;
            this.heardCount = (heardCount != null) ? heardCount : 0L;
            this.notHeardCount = (notHeardCount != null) ? notHeardCount : 0L;
            this.beCarefulCount = (beCarefulCount != null) ? beCarefulCount : 0L;
            this.totalParticipants = this.heardCount + this.notHeardCount + this.beCarefulCount;
            this.totalEligibleVoters = null;
        }
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
        private BigDecimal maxDb; //최대 데시벨
        private BigDecimal avgDb; // 평균 데시벨
        private Map<VoteType, Long> voteCounts; // 투표 현황
        private List<CommentResponse.CommentDto> comments; // 댓글 목록
    }
}
