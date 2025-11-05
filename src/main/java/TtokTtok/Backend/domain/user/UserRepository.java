package TtokTtok.Backend.domain.user;

import TtokTtok.Backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {


}
