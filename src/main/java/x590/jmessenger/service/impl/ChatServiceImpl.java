package x590.jmessenger.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import x590.jmessenger.entities.Chat;
import x590.jmessenger.repository.ChatRepository;
import x590.jmessenger.service.ChatService;

import java.util.Optional;

@Component
public class ChatServiceImpl extends AbstractServiceImpl<Chat, Integer, ChatRepository> implements ChatService {

	@Autowired
	private ChatServiceImpl(ChatRepository repository) {
		super(repository);
	}

	@Override
	public Optional<Chat> findById(int id) {
		return getRepository().findById(id);
	}
}
