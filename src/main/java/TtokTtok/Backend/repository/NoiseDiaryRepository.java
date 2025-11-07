package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import TtokTtok.Backend.web.dto.ReportDto; // (ReportDto가 정의되어 있다고 가정)
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

    // 특정 사용자의 총 소음 기록 수(삭제되지 않은 것만)
    long countByUserAndDeletedFalse(User user);

    // 특정 사용자, 기간 내 소음 기록 수(삭제되지 않은 것만)
    long countByUserAndCreatedAtBetweenAndDeletedFalse(User user, LocalDateTime start, LocalDateTime end);

    // 소음 현황판에 표시될 리포트 (reportYn이 true인 NoiseDiary) 목록 조회
    Page<NoiseDiary> findAllByUser_ApartmentAndReportYnOrderByReportedAtDesc(Apartment apartment, Boolean reportYn, Pageable pageable);

    // 같은 동의 소음 현황판 리포트 목록 조회
    Page<NoiseDiary> findAllByUser_ApartmentAndUser_DongAndReportYnOrderByReportedAtDesc(Apartment apartment, Integer dong, Boolean reportYn, Pageable pageable);



    // TODO:  -> 캘린더 기능에서 날짜 선택 안 한 default 상태에서 어떻게?? 물어보고 수정할 것
    // 특정 사용자, 소음 기록 전체 조회 (삭제되지 않은 것만)
    List<NoiseDiary> findByUserAndDeletedFalse(User user);

    // 특정 사용자, 기간 내 소음 기록 조회 (삭제되지 않은 것만)
    List<NoiseDiary> findByUserAndCreatedAtBetweenAndDeletedFalseOrderByCreatedAtAsc(
            User user,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // soft delete에서 개별 레코드 조회용
    Optional<NoiseDiary> findByIdAndUserAndDeletedFalse(Long id, User user);

    // 특정 사용자, 평균 dB 반환(삭제되지 않은 것만)
    @Query("""
        SELECT AVG(n.dbAvg) 
        FROM NoiseDiary n 
        WHERE n.user = :user 
          AND n.deleted = false
    """)
    Double findAverageDbByUser(@Param("user") User user);

    // 월간 캘린더에서 날짜별 기록 존재 여부 확인(삭제되지 않은 것만)
    @Query("""
        SELECT DISTINCT FUNCTION('DATE', n.createdAt)
        FROM NoiseDiary n
        WHERE n.user.id = :userId
          AND n.deleted = false
          AND FUNCTION('DATE', n.createdAt) BETWEEN :startDate AND :endDate
    """)
    List<LocalDate> findAllDatesByUserAndMonth(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 특정 날짜의 소음 기록 조회 (캘린더 클릭 시 상세보기용, 삭제되지 않은 것만)
    @Query("""
        SELECT n FROM NoiseDiary n
        WHERE n.user.id = :userId
          AND n.deleted = false
          AND FUNCTION('DATE', n.createdAt) = :targetDate
        ORDER BY n.createdAt ASC
    """)
    List<NoiseDiary> findByUserAndDate(
            @Param("userId") Long userId,
            @Param("targetDate") LocalDate targetDate
    );

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