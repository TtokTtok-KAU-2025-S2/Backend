package TtokTtok.Backend.web.dto;

import TtokTtok.Backend.common.enums.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class VoteResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteDto {
        private Long voteId;
        private VoteType voteType;
        private LocalDateTime createdAt;
    }
}