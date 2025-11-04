package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
}