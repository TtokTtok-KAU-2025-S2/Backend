package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import TtokTtok.Backend.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface VoteRepository  extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndNoiseDiary(User user, NoiseDiary noiseDiary);

    // 특정 NoiseDiary에 대한 각 VoteType 별 투표 수 집계
    @Query("SELECT v.type, COUNT(v.id) FROM Vote v WHERE v.noiseDiary = :noiseDiary AND v.type IS NOT NULL GROUP BY v.type")
    List<Object[]> countVotesByTypeForNoiseDiary(@Param("noiseDiary") NoiseDiary noiseDiary);

    void deleteByUserAndNoiseDiary(User user, NoiseDiary noiseDiary);
}