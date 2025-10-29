package TtokTtok.Backend.repository;

import TtokTtok.Backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByAptUnit_AptCodeAndAptUnit_DongAndAptUnit_HoIn(String aptCode, Integer dong, List<Integer> hoList);
}
