package x590.jmessenger.service;

import x590.jmessenger.entity.Message;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface MessageService {

	Message findMessageById(int id);

	List<Message> getMessages();

	boolean saveMessage(Message message);

	void updateMessage(Message message, String newText);

	void delete(Message message);

	Set<Message> findMessagesInChatSince(int chatId, Timestamp since);
}
