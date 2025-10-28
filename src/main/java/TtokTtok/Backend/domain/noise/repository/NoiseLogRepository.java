package TtokTtok.Backend.domain.noise.repository;

import TtokTtok.Backend.domain.noise.entity.NoiseLog;
import TtokTtok.Backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoiseLogRepository extends JpaRepository<NoiseLog, Long> {
    List<NoiseLog> findByUserAndLogDatetimeBetweenOrderByLogDatetimeDesc(User user, LocalDateTime start, LocalDateTime end);

    Long countByUserAndLogDatetimeBetween(User user, LocalDateTime startDatetime, LocalDateTime endDatetime);

    //월간 소음 평균 강도를 계산하는 쿼리
    @Query("SELECT AVG(nl.noiseLevel) FROM NoiseLog nl WHERE nl.user = :user AND nl.logDatetime BETWEEN :startDatetime AND :endDatetime")
    Optional<Double> findAverageNoiseLevelByUserAndLogDatetimeBetween(
            @Param("user") User user,
            @Param("startDatetime") LocalDateTime startDatetime,
            @Param("endDatetime") LocalDateTime endDatetime
    );

    List<NoiseLog> findByUserAndLogDatetimeBetween(User user, LocalDateTime logDatetimeAfter, LocalDateTime logDatetimeBefore);
}