package x590.jmessenger.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import x590.jmessenger.entity.Message;
import x590.jmessenger.repository.MessageRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

	private final MessageRepository repository;

	@Override
	public Message findMessageById(int id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cannot find message"));
	}

	@Override
	public List<Message> getMessages() {
		return repository.findAll();
	}

	@Override
	public boolean saveMessage(Message message) {
		repository.save(message);
		return true;
	}

	@Override
	public void updateMessage(Message message, String newText) {
		message.setText(newText);
		repository.save(message);
	}

	public void delete(Message message) {
		repository.delete(message);
	}

	@Override
	public Set<Message> findMessagesInChatSince(int chatId, Timestamp since) {
		return repository.findMessagesInChatSince(chatId, since);
	}
}
