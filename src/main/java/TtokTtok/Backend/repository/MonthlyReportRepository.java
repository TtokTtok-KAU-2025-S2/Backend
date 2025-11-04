package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {
    /**
     * 아파트 ID와 연도, 월을 기준으로 리포트를 조회합니다.
     */
    Optional<MonthlyReport> findByApartmentIdAndYearAndMonth(Long apartmentId, int year, int month);
}