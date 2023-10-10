package x590.jmessenger.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import x590.jmessenger.entity.Chat;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

	boolean existsByTitle(String title);

	List<Chat> findByTitleContainingIgnoreCaseOrderByTitle(String part, Pageable pageable);
}
