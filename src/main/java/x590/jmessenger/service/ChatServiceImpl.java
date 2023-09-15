package x590.jmessenger.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import x590.jmessenger.entity.Chat;
import x590.jmessenger.repository.ChatRepository;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;

	@Autowired
	public ChatServiceImpl(ChatRepository chatRepository) {
		this.chatRepository = chatRepository;
	}

	@Override
	public Chat findChatById(int id) {
		return chatRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cannot find chat"));
	}

//	@Override
//	public List<Chat> getChats() {
//		return chatRepository.findAll();
//	}
//
//	@Override
//	public boolean saveChat(Chat chat, BindingResult bindingResult) {
//		return false;
//	}
//
//	@Override
//	public void updateChat(Chat existingChat, Chat newChatData) {
//
//	}

	@Override
	public boolean deleteChat(int id) {
		if (chatRepository.existsById(id)) {
			chatRepository.deleteById(id);
			return true;
		}

		return false;
	}
}
