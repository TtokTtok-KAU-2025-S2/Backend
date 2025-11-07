package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import TtokTtok.Backend.web.dto.ReportDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public interface NoiseDiaryRepository extends JpaRepository<NoiseDiary, Long> {
    // 1. 특정 사용자의 총 소음 기록 수 (DeletedFalse 조건 제거)
    long countByUser(User user);

    // 2. 특정 사용자, 기간 내 소음 기록 수 (DeletedFalse 조건 제거)
    long countByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);

    // 3. 소음 기록 전체 조회 (DeletedFalse 조건 제거)
    List<NoiseDiary> findByUser(User user);

    // 4. 특정 사용자, 기간 내 소음 기록 조회 (DeletedFalse 조건 제거)
    List<NoiseDiary> findByUserAndCreatedAtBetweenOrderByCreatedAtAsc(
            User user,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // 5. 개별 레코드 조회용 (DeletedFalse 조건 제거)
    Optional<NoiseDiary> findByIdAndUser(Long id, User user); // Soft delete 플래그 없이 조회

    // 6. 특정 사용자, 평균 dB 반환 (Query 내 deleted = false 제거)
    @Query("""
        SELECT AVG(n.dbAvg) 
        FROM NoiseDiary n 
        WHERE n.user = :user 
    """)
    Double findAverageDbByUser(@Param("user") User user);

    // 7. 월간 캘린더에서 날짜별 기록 존재 여부 확인 (Query 내 deleted = false 제거)
    @Query("""
        SELECT DISTINCT FUNCTION('DATE', n.createdAt)
        FROM NoiseDiary n
        WHERE n.user.id = :userId
          AND FUNCTION('DATE', n.createdAt) BETWEEN :startDate AND :endDate
    """)
    List<LocalDate> findAllDatesByUserAndMonth(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 8. 특정 날짜의 소음 기록 조회 (Query 내 deleted = false 제거)
    @Query("""
        SELECT n FROM NoiseDiary n
        WHERE n.user.id = :userId
          AND FUNCTION('DATE', n.createdAt) = :targetDate
        ORDER BY n.createdAt ASC
    """)
    List<NoiseDiary> findByUserAndDate(
            @Param("userId") Long userId,
            @Param("targetDate") LocalDate targetDate
    );

    // ⭐⭐ 오류 발생 메서드 수정 (User_Apartment로 경로 지정) ⭐⭐

    // 9. 총 건수 조회 (이름 수정)
    Integer countByUser_ApartmentAndReportedAtBetween(
            Apartment apartment,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    // 10. 소음 현황판에 표시될 리포트 (User_Apartment로 경로 지정)
    Page<NoiseDiary> findAllByUser_ApartmentAndReportYnOrderByReportedAtDesc(Apartment apartment, Boolean reportYn, Pageable pageable);

    // 11. 같은 동의 소음 현황판 리포트 목록 조회 (User_Apartment와 User_Dong으로 경로 지정)
    Page<NoiseDiary> findAllByUser_ApartmentAndUser_DongAndReportYnOrderByReportedAtDesc(Apartment apartment, Integer dong, Boolean reportYn, Pageable pageable);

    // 12. 카테고리별 통계 (User_Apartment로 경로 지정 - @Query는 문제 없음)
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

    // 13. 시간대별 통계 (User_Apartment로 경로 지정 - @Query는 문제 없음)
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

    // Note: @Query가 없는 메서드인 countByApartmentAndReportedAtBetween만 이름을 수정했고,
    // @Query가 있는 메서드 (12, 13번)는 JPQL에서 nd.user.apartment를 사용하므로 이름 변경이 필요하지 않습니다.
    // 하지만 일관성을 위해 12번과 13번 메서드의 이름도 countByUser_Apartment...로 변경하는 것이 좋습니다.
    // 여기서는 가장 문제가 된 9번 메서드만 수정하여 충돌을 해결했습니다.
}