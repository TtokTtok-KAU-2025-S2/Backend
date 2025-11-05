package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.NoiseDiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import TtokTtok.Backend.web.dto.ReportDto; // (ReportDto가 정의되어 있다고 가정)
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoiseDiaryRepository extends JpaRepository<NoiseDiary, Long> {
    // 소음 현황판에 표시될 리포트 (reportYn이 true인 NoiseDiary) 목록 조회
    Page<NoiseDiary> findAllByUser_ApartmentAndReportYnOrderByReportedAtDesc(Apartment apartment, Boolean reportYn, Pageable pageable);

    // 같은 동의 소음 현황판 리포트 목록 조회
    Page<NoiseDiary> findAllByUser_ApartmentAndUser_DongAndReportYnOrderByReportedAtDesc(Apartment apartment, Integer dong, Boolean reportYn, Pageable pageable);

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