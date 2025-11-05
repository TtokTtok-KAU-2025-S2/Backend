package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
}