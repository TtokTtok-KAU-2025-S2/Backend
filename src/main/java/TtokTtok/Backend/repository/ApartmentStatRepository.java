package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import TtokTtok.Backend.domain.ApartmentStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 1. Query 임포트
import org.springframework.data.repository.query.Param; // 2. Param 임포트

import java.util.List; // 3. List 임포트
import java.util.Optional;

public interface ApartmentStatRepository extends JpaRepository<ApartmentStat, Long> {

    Optional<ApartmentStat> findByApartment(Apartment apartment);

    // --- (이 메서드를 추가하세요) ---
    /**
     * (QueryService용) 아파트 이름(keyword)을 포함하는 통계를 검색합니다.
     * s.apartment.name (ApartmentStat -> Apartment -> name)을 기준으로 검색합니다.
     */
    @Query("SELECT s FROM ApartmentStat s " +
            "WHERE s.apartment.name LIKE CONCAT('%', :keyword, '%')")
    List<ApartmentStat> findByApartmentNameContaining(@Param("keyword") String keyword);
    // --- (여기까지) ---
}