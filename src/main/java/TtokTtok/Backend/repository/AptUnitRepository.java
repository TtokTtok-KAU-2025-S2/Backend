package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.complex.Apartment;
import TtokTtok.Backend.domain.complex.AptUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AptUnitRepository extends JpaRepository<AptUnit, Long> {
    Optional<AptUnit> findByApartmentAndDongAndHo(Apartment apartment, Integer dong, Integer ho);
}

