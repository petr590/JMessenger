package x590.jmessenger.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.Chat;
import x590.jmessenger.repository.ChatRepository;
import x590.jmessenger.util.Util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

	private final ChatRepository repository;
	private final Util util;

	@Override
	public Chat findChatById(int id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cannot find chat"));
	}

	@Override
	public List<Chat> searchChats(String titleSubstr, int pageNumber, int pageSize) {
		return repository.findByTitleContainingIgnoreCaseOrderByTitle(titleSubstr, PageRequest.of(pageNumber, pageSize));
	}

	@Override
	public boolean saveChat(Chat chat, @Nullable MultipartFile picture, BindingResult bindingResult)
			throws IOException, SQLException {

		checkTitle(chat, bindingResult);

		util.setPictureIfNotEmpty(picture, chat, bindingResult);

		if (bindingResult.hasErrors()) {
			return false;
		}

		chat.setId(0);
		repository.save(chat);
		return true;
	}

	@Override
	public void updateChat(Chat chat) {
		repository.save(chat);
	}

	@Override
	public void updateChat(Chat existingChat, Chat newChatData, BindingResult bindingResult) {
		var title = newChatData.getTitle();

		if (title != null && !existingChat.getTitle().equalsIgnoreCase(title)) {
			checkTitle(newChatData, bindingResult);

			if (bindingResult.hasErrors()) {
				return;
			}
		}

		existingChat.update(newChatData);
		repository.save(existingChat);

	}

	private void checkTitle(Chat chat, BindingResult bindingResult) {
		if (repository.existsByTitle(chat.getTitle())) {
			bindingResult.rejectValue("title", "error.chat.alreadyExists");
		}
	}

	@Override
	public void deleteChat(Chat chat) {
		repository.delete(chat);
	}
}
