package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.user.TrustIndex;
import TtokTtok.Backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

// 인터페이스명 TrustIndexLogRepository -> TrustIndexRepository
public interface TrustIndexRepository extends JpaRepository<TrustIndex, Long> { // 엔티티명 변경

    // 최근 활동 5개 조회 (화면 표시용)
    // 메서드명 변경 및 엔티티명 변경
    List<TrustIndex> findTop5ByUserOrderByLogDateDesc(User user);

    // 월별 점수 변화량 합계 조회 (과거 6개월치)
    // 테이블명, 컬럼명 변경
    @Query(value = "SELECT YEAR(log_date) as year, MONTH(log_date) as month, SUM(change_amount) as totalChange " +
            "FROM trust_index " + // trust_index_log -> trust_index
            "WHERE user_id = :userId AND log_date >= :startDate " +
            "GROUP BY year, month " +
            "ORDER BY year ASC, month ASC " +
            "LIMIT 6", nativeQuery = true)
    List<MonthlyTrustChange> findMonthlyChangesByUserLast6Months(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);

    // Native Query 결과를 받을 인터페이스 (Projections)
    interface MonthlyTrustChange {
        Integer getYear();
        Integer getMonth();
        Double getTotalChange();
    }
}
