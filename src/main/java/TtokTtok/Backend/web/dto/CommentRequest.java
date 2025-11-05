package TtokTtok.Backend.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class CommentRequest {

    @Getter
    public static class CreateCommentDto {
        @NotBlank(message = "댓글 내용은 필수 입력 값입니다.")
        private String content;
    }

    @Getter
    public static class UpdateCommentDto {
        @NotBlank(message = "댓글 내용은 필수 입력 값입니다.")
        private String content;
    }
}