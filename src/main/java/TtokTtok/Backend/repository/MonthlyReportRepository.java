package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {

    /**
     * (QueryService용) 아파트 ID와 년/월을 기준으로 미리 생성된 리포트를 조회
     */
    Optional<MonthlyReport> findByApartmentIdAndYearAndMonth(Long apartmentId, int year, int month);
}