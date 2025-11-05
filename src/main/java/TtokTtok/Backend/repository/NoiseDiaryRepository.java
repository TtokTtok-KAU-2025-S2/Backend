package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.web.dto.ReportDto; // (ReportDto가 정의되어 있다고 가정)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoiseDiaryRepository extends JpaRepository<NoiseDiary, Long> {

    // 1. 총 건수 조회
    @Query("SELECT COUNT(nd) FROM NoiseDiary nd " +
            "WHERE nd.user.apartment = :apartment AND nd.reportYn = true " +
            "AND nd.reportedAt BETWEEN :startTime AND :endTime")
    Integer countByApartmentAndReportedAtBetween(
            @Param("apartment") Apartment apartment,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // 2. 카테고리별 통계 (SELECT new DTO 사용)
    @Query("SELECT new TtokTtok.Backend.web.dto.ReportDto$CategoryStatDto(nd.category, COUNT(nd)) " +
            "FROM NoiseDiary nd " +
            "WHERE nd.user.apartment = :apartment AND nd.reportYn = true " +
            "AND nd.reportedAt BETWEEN :startTime AND :endTime " +
            "GROUP BY nd.category")
    List<ReportDto.CategoryStatDto> findCategoryStatsByApartmentAndReportedAtBetween(
            @Param("apartment") Apartment apartment,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // 3. 시간대별 통계 (SELECT new DTO 사용)
    @Query("SELECT new TtokTtok.Backend.web.dto.ReportDto$HourlyStatDto(HOUR(nd.reportedAt), COUNT(nd)) " +
            "FROM NoiseDiary nd " +
            "WHERE nd.user.apartment = :apartment AND nd.reportYn = true " +
            "AND nd.reportedAt BETWEEN :startTime AND :endTime " +
            "GROUP BY HOUR(nd.reportedAt)")
    List<ReportDto.HourlyStatDto> findHourlyStatsByApartmentAndReportedAtBetween(
            @Param("apartment") Apartment apartment,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}