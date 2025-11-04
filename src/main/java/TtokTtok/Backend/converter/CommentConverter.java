package TtokTtok.Backend.converter;

import TtokTtok.Backend.domain.ReportComment;
import TtokTtok.Backend.web.dto.CommentResponse;

public class CommentConverter {

    public static CommentResponse.CommentDto toCommentDto(ReportComment comment, Boolean isMyComment) {
        return CommentResponse.CommentDto.builder()
            .commentId(comment.getId())
            .authorDong(comment.getUser().getDong())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .isMyComment(isMyComment)
            .build();
        }
}