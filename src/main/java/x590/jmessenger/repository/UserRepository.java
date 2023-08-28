package x590.jmessenger.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import x590.jmessenger.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Modifying
	@Query("UPDATE User SET name = :name WHERE id = :id")
	boolean updateById(int userId, String name);
}
