package x590.jmessenger.service;

import x590.jmessenger.entity.Message;

import java.util.List;

public interface MessageService {

	Message findMessageById(int id);

	List<Message> getMessages();

	boolean saveMessage(Message message);

	void updateChat(Message existingMessage, Message newMessageData);

	void delete(Message message);
}
