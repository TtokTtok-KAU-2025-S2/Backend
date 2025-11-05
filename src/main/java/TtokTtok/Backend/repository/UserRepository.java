package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email); // 이메일 중복 확인을 위한 메서드 추가
}