package TtokTtok.Backend.service;

import TtokTtok.Backend.apiPayload.code.status.ErrorStatus;
import TtokTtok.Backend.apiPayload.exception.GeneralException;
import TtokTtok.Backend.config.jwt.SecurityUtil;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.Vote;
import TtokTtok.Backend.repository.NoiseDiaryRepository;
import TtokTtok.Backend.repository.UserRepository;
import TtokTtok.Backend.repository.VoteRepository;
import TtokTtok.Backend.web.dto.VoteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final NoiseDiaryRepository noiseDiaryRepository;

    @Override
    public Vote createOrUpdateVote(Long reportId, VoteRequest.CreateVoteDto request) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        NoiseDiary noiseDiary = noiseDiaryRepository.findById(reportId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        Optional<Vote> existingVote = voteRepository.findByUserAndNoiseDiary(user, noiseDiary);

        Vote vote;
        if (existingVote.isPresent()) {
            // 이미 투표한 경우, 투표 내용을 변경
            vote = existingVote.get();
            vote.updateType(request.getVoteType());
        } else {
            // 새로 투표하는 경우
            vote = Vote.builder()
                    .user(user)
                    .noiseDiary(noiseDiary)
                    .type(request.getVoteType())
                    .build();
        }
        return voteRepository.save(vote);
    }

    @Override
    public void deleteVote(Long reportId) {
        String userEmail = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        NoiseDiary noiseDiary = noiseDiaryRepository.findById(reportId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));

        voteRepository.deleteByUserAndNoiseDiary(user, noiseDiary);
    }
}