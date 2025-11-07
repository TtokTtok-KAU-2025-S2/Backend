package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.ReportComment;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.ReportCommentRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.web.dto.CommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final ReportCommentRepository reportCommentRepository;
    private final UserRepository userRepository;
    private final NoiseDiaryRepository noiseDiaryRepository;

    @Override
    public ReportComment createComment(Long reportId, CommentRequest.CreateCommentDto request) {
    String userEmail = SecurityUtil.getCurrentUserEmail();
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

    NoiseDiary noiseDiary = noiseDiaryRepository.findById(reportId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

    ReportComment newComment = ReportComment.builder()
            .user(user)
            .noiseDiary(noiseDiary)
            .content(request.getContent())
            .build();

    return reportCommentRepository.save(newComment);
}

    @Override
    public ReportComment updateComment(Long commentId, CommentRequest.UpdateCommentDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        ReportComment comment = reportCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        comment.updateContent(request.getContent());
        return comment;
    }

    @Override
    public void deleteComment(Long commentId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        ReportComment comment = reportCommentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        reportCommentRepository.delete(comment);
    }
}