package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.common.enums.VoteType;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.converter.CommentConverter;
import TtokTtok.Backend.converter.NoiseReportConverter;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.ReportComment;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.ReportCommentRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.repository.VoteRepository;
import TtokTtok.Backend.web.dto.CommentResponse;
import TtokTtok.Backend.web.dto.NoiseReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoiseReportServiceImpl implements NoiseReportService {

    private final NoiseDiaryRepository noiseDiaryRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final ReportCommentRepository reportCommentRepository;

    @Override
    public NoiseReportResponse.NoiseReportListResponse getNoiseReportList(Pageable pageable) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Page<NoiseDiary> noiseDiaryPage;
        noiseDiaryPage = noiseDiaryRepository.findAllByUser_ApartmentAndUser_DongAndReportYnOrderByReportedAtDesc(
                user.getApartment(), user.getDong(), true, pageable);


        return NoiseReportConverter.toNoiseReportListResponse(noiseDiaryPage);
    }

    @Override
    public NoiseReportResponse.NoiseReportDetailDto getNoiseReportDetail(Long reportId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        NoiseDiary noiseDiary = noiseDiaryRepository.findById(reportId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        Map<VoteType, Long> voteCounts = new java.util.EnumMap<>(VoteType.class);
        for (VoteType type : VoteType.values()) {
            voteCounts.put(type, 0L);
        }
        Map<VoteType, Long> dbVoteCounts = voteRepository.countVotesByTypeForNoiseDiary(noiseDiary);
        voteCounts.putAll(dbVoteCounts);

        List<ReportComment> comments = reportCommentRepository.findAllByNoiseDiaryOrderByCreatedAtAsc(noiseDiary);
        List<CommentResponse.CommentDto> commentDtos = comments.stream()
                .map(comment -> {
                    boolean isMyComment = comment.getUser().getId().equals(user.getId());
                    return CommentConverter.toCommentDto(comment, isMyComment);
                })
                .collect(Collectors.toList());

        return NoiseReportConverter.toNoiseReportDetailDto(noiseDiary,  voteCounts, commentDtos);
    }

}  






















