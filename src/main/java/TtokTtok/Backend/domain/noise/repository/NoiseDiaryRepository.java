package TtokTtok.Backend.domain.noise.repository;

import TtokTtok.Backend.domain.NoiseDiary;
import TtokTtok.Backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoiseDiaryRepository extends JpaRepository<NoiseDiary, Long> {

    // 특정 사용자의 총 소음 기록 수(삭제되지 않은 것만)
    long countByUserAndDeletedFalse(User user);

    // 특정 사용자, 기간 내 소음 기록 수(삭제되지 않은 것만)
    long countByUserAndCreatedAtBetweenAndDeletedFalse(User user, LocalDateTime start, LocalDateTime end);


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
}

