package x590.jmessenger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import x590.jmessenger.entity.Message;

import java.sql.Timestamp;
import java.util.Set;

public interface MessageRepository extends JpaRepository<Message, Integer> {

	@Query("SELECT msg FROM Message msg WHERE msg.chat.id = :chatId AND msg.publicationTime >= :since")
	Set<Message> findMessagesInChatSince(@Param("chatId") int chatId, @Param("since") Timestamp since);
}
