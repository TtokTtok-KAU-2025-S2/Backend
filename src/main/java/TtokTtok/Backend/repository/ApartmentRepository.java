package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.complex.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    Optional<Apartment> findByAptCode(String aptCode);
    Optional<Apartment> findByAptName(String aptName);
}

