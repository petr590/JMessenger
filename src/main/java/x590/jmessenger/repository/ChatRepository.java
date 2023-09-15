package x590.jmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import x590.jmessenger.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

	Chat findByTitle(String title);

	boolean existsByTitle(String title);
}
