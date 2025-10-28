package TtokTtok.Backend.domain.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 추가 쿼리 필요 시 여기에 작성
}