package x590.jmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import x590.jmessenger.entities.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {}
