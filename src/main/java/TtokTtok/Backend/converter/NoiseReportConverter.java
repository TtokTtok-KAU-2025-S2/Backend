package TtokTtok.Backend.converter;

import TtokTtok.Backend.common.enums.VoteType;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.web.dto.CommentResponse;
import TtokTtok.Backend.web.dto.NoiseReportResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class NoiseReportConverter {

    public static NoiseReportResponse.NoiseReportPreviewDto toNoiseReportPreviewDto(NoiseDiary noiseDiary) {
        return NoiseReportResponse.NoiseReportPreviewDto.builder()
                .reportId(noiseDiary.getId())
                .authorDong(noiseDiary.getUser().getDong())
                .reportedAt(noiseDiary.getReportedAt())
                .category(noiseDiary.getCategory())
                .summary(noiseDiary.getSummary())
                .build();
    }

    public static NoiseReportResponse.NoiseReportListResponse toNoiseReportListResponse(
            Page<NoiseReportResponse.NoiseReportPreviewDto> noiseReportPreviewDtoPage) {
                return NoiseReportResponse.NoiseReportListResponse.builder()
                        .reports(noiseReportPreviewDtoPage.getContent())
                        .listSize(noiseReportPreviewDtoPage.getContent().size())
                        .totalPage(noiseReportPreviewDtoPage.getTotalPages())
                        .totalElements(noiseReportPreviewDtoPage.getTotalElements())
                        .isFirst(noiseReportPreviewDtoPage.isFirst())
                        .isLast(noiseReportPreviewDtoPage.isLast())
                        .build();

    }

    public static NoiseReportResponse.NoiseReportDetailDto toNoiseReportDetailDto(
            NoiseDiary noiseDiary, Map<VoteType, Long> voteCounts, List<CommentResponse.CommentDto> comments,
            BigDecimal maxDb, BigDecimal avgDb)
    {
        return NoiseReportResponse.NoiseReportDetailDto.builder()
                .reportId(noiseDiary.getId())
                .authorDong(noiseDiary.getUser().getDong())
                .reportedAt(noiseDiary.getReportedAt())
                .category(noiseDiary.getCategory())
                .summary(noiseDiary.getSummary())
                .maxDb(maxDb)
                .avgDb(avgDb)
                .voteCounts(voteCounts)
                .comments(comments)
                .build();
    }
}




















