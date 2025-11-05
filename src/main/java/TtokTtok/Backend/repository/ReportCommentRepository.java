package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.ReportComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {
    List<ReportComment> findAllByNoiseDiaryOrderByCreatedAtAsc(NoiseDiary noiseDiary);
}