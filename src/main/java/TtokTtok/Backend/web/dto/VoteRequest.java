package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class VoteRequest {

    @Getter
    public static class CreateVoteDto {
        @NotNull(message = "투표 타입은 필수 입력 값입니다.")
        private VoteType voteType;
    }
}