package TtokTtok.Backend.web.controller;

import TtokTtok.Backend.apiPayload.ApiResponse;
import TtokTtok.Backend.converter.CommentConverter;
import TtokTtok.Backend.domain.ReportComment;
import TtokTtok.Backend.service.CommentService;
import TtokTtok.Backend.web.dto.CommentRequest;
import TtokTtok.Backend.web.dto.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // Base path for comments
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/noise-reports/{reportId}/comments")
    public ApiResponse<CommentResponse.CommentDto> createComment(
            @PathVariable Long reportId,
            @RequestBody @Valid CommentRequest.CreateCommentDto request) {
        ReportComment comment = commentService.createComment(reportId, request);
        // isMyComment는 현재 로그인한 사용자가 작성한 댓글이므로 true
        return ApiResponse.onSuccess(CommentConverter.toCommentDto(comment, true));
    }

    @PutMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse.CommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest.UpdateCommentDto request) {
        ReportComment comment = commentService.updateComment(commentId, request);
        // isMyComment는 현재 로그인한 사용자가 수정한 댓글이므로 true
        return ApiResponse.onSuccess(CommentConverter.toCommentDto(comment, true));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<String> deleteComment(
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.onSuccess("댓글이 성공적으로 삭제되었습니다.");
    }
}