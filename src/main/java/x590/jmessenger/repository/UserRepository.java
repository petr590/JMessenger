package x590.jmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import x590.jmessenger.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);

	boolean existsByUsername(String username);
}
