package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.ReportComment;
import TtokTtok.Backend.web.dto.CommentRequest;

public interface CommentService {
    ReportComment createComment(Long reportId, CommentRequest.CreateCommentDto request);
    ReportComment updateComment(Long commentId, CommentRequest.UpdateCommentDto request);
    void deleteComment(Long commentId);
}