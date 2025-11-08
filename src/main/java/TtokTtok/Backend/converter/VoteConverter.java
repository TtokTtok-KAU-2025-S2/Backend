package TtokTtok.Backend.converter;

import TtokTtok.Backend.domain.Vote;
import TtokTtok.Backend.web.dto.VoteResponse;


public class VoteConverter {

    public static VoteResponse.VoteDto toVoteDto(Vote vote) {
        return VoteResponse.VoteDto.builder()
                .voteId(vote.getId())
                .voteType(vote.getType())
                .createdAt(vote.getCreatedAt())
                .build();
    }
}  