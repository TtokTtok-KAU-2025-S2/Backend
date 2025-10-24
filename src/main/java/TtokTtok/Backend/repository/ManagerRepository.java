package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.complex.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByAptName(String aptName);
}

