package TtokTtok.Backend.service;

import TtokTtok.Backend.domain.Vote;
import TtokTtok.Backend.web.dto.VoteRequest;

public interface VoteService {
    Vote createOrUpdateVote(Long reportId, VoteRequest.CreateVoteDto request);
    void deleteVote(Long reportId);
}