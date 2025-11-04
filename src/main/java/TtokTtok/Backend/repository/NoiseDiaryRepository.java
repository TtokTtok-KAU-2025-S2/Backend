package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.NoiseDiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoiseDiaryRepository extends JpaRepository<NoiseDiary, Long> {
    // 소음 현황판에 표시될 리포트 (reportYn이 true인 NoiseDiary) 목록 조회
    Page<NoiseDiary> findAllByUser_ApartmentAndReportYnOrderByReportedAtDesc(Apartment apartment, Boolean reportYn, Pageable pageable);

    // 같은 동의 소음 현황판 리포트 목록 조회
    Page<NoiseDiary> findAllByUser_ApartmentAndUser_DongAndReportYnOrderByReportedAtDesc(Apartment apartment, Integer dong, Boolean reportYn, Pageable pageable);
}