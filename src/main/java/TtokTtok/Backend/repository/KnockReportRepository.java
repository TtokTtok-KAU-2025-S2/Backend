package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.knock.KnockReport;
import TtokTtok.Backend.domain.knock.KnockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnockReportRepository extends JpaRepository<KnockReport, Long> {

    // 특정 KnockRequest에 대한 Report가 존재하는지 확인 (중복 보고 방지용)
    boolean existsByKnockRequest(KnockRequest knockRequest);

    // --- 관리자 조회용 메서드 추가 (Join Fetch 사용) ---
    /**
     * 특정 아파트 단지(aptCode)에 보고된 모든 '똑똑' 요청 목록을
     * 보고 시간(reportTime) 최신순으로 조회합니다. (Join Fetch로 KnockRequest 정보 포함)
     * @param aptCode 아파트 단지 코드
     * @return List<KnockReport> 보고된 기록 목록 (KnockRequest 포함)
     */
    @Query("SELECT kr FROM KnockReport kr JOIN FETCH kr.knockRequest k " +
            "WHERE k.requester.aptUnit.aptCode = :aptCode " +
            "ORDER BY kr.reportTime DESC")
    List<KnockReport> findReportsByAptCodeWithKnockRequest(@Param("aptCode") String aptCode);
    // --- 추가 종료 ---

}