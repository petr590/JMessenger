package x590.jmessenger.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import x590.jmessenger.entity.Message;
import x590.jmessenger.repository.MessageRepository;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

	private final MessageRepository repository;

	@Autowired
	public MessageServiceImpl(MessageRepository repository) {
		this.repository = repository;
	}

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
		return false;
	}

	@Override
	public void updateChat(Message existingMessage, Message newMessageData) {

	}

	public void delete(Message message) {
		repository.delete(message);
	}
}
