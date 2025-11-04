package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.web.dto.ReportDto; // 1. 여기는 점(.)을 씁니다.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoiseDiaryRepository extends JpaRepository<NoiseDiary, Long> {

    // 1. 총 건수 조회 (변경 없음)
    @Query("SELECT COUNT(nd) FROM NoiseDiary nd " +
            "WHERE nd.user.apartment = :apartment AND nd.reportYn = true " +
            "AND nd.reportedAt BETWEEN :startTime AND :endTime")
    Integer countByApartmentAndReportedAtBetween(
            @Param("apartment") Apartment apartment,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // 2. 카테고리별 통계
    // Java 리턴 타입은 점(.) 사용
    @Query("SELECT new TtokTtok.Backend.web.dto.ReportDto$CategoryStatDto(nd.category, COUNT(nd)) " + // JPQL 쿼리는 달러($) 사용
            "FROM NoiseDiary nd " +
            "WHERE nd.user.apartment = :apartment AND nd.reportYn = true " +
            "AND nd.reportedAt BETWEEN :startTime AND :endTime " +
            "GROUP BY nd.category")
    List<ReportDto.CategoryStatDto> findCategoryStatsByApartmentAndReportedAtBetween( // 2. 여기도 점(.)을 씁니다.
                                                                                      @Param("apartment") Apartment apartment,
                                                                                      @Param("startTime") LocalDateTime startTime,
                                                                                      @Param("endTime") LocalDateTime endTime
    );

    // 3. 시간대별 통계
    // Java 리턴 타입은 점(.) 사용
    @Query("SELECT new TtokTtok.Backend.web.dto.ReportDto$HourlyStatDto(HOUR(nd.reportedAt), COUNT(nd)) " + // JPQL 쿼리는 달러($) 사용
            "FROM NoiseDiary nd " +
            "WHERE nd.user.apartment = :apartment AND nd.reportYn = true " +
            "AND nd.reportedAt BETWEEN :startTime AND :endTime " +
            "GROUP BY HOUR(nd.reportedAt)")
    List<ReportDto.HourlyStatDto> findHourlyStatsByApartmentAndReportedAtBetween( // 3. 여기도 점(.)을 씁니다.
                                                                                  @Param("apartment") Apartment apartment,
                                                                                  @Param("startTime") LocalDateTime startTime,
                                                                                  @Param("endTime") LocalDateTime endTime
    );
}