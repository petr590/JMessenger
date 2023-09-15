package x590.jmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import x590.jmessenger.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {}
