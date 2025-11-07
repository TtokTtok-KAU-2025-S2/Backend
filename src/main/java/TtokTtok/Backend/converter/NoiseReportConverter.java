package TtokTtok.Backend.converter;

import TtokTtok.Backend.common.enums.VoteType;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.web.dto.CommentResponse;
import TtokTtok.Backend.web.dto.NoiseReportResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static NoiseReportResponse.NoiseReportListResponse toNoiseReportListResponse(Page<NoiseDiary> noiseDiaryPage) {
        List<NoiseReportResponse.NoiseReportPreviewDto> reportPreviewDtoList = noiseDiaryPage.stream()
                .map(NoiseReportConverter::toNoiseReportPreviewDto).collect(Collectors.toList());

        return NoiseReportResponse.NoiseReportListResponse.builder()
                .reports(reportPreviewDtoList)
                .listSize(reportPreviewDtoList.size())
                .totalPage(noiseDiaryPage.getTotalPages())
                .totalElements(noiseDiaryPage.getTotalElements())
                .isFirst(noiseDiaryPage.isFirst())
                .isLast(noiseDiaryPage.isLast())
                .build();
    }

    public static NoiseReportResponse.NoiseReportDetailDto toNoiseReportDetailDto(
            NoiseDiary noiseDiary, Map<VoteType, Long> voteCounts, List<CommentResponse.CommentDto> comments) {
        return NoiseReportResponse.NoiseReportDetailDto.builder()
                .reportId(noiseDiary.getId())
                .authorDong(noiseDiary.getUser().getDong())
                .reportedAt(noiseDiary.getReportedAt())
                .category(noiseDiary.getCategory())
                .summary(noiseDiary.getSummary())
                .voteCounts(voteCounts)
                .comments(comments)
                .build();
    }
}




















