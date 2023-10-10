package x590.jmessenger.service;

import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.Chat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ChatService {

	Chat findChatById(int id);

	List<Chat> searchChats(String titleSubstr, int pageNumber, int pageSize);

	boolean saveChat(Chat chat, @Nullable MultipartFile picture, BindingResult bindingResult) throws IOException, SQLException;

	void updateChat(Chat chat);

	void updateChat(Chat chat, Chat newChatData, BindingResult bindingResult);

	void deleteChat(Chat chat);
}
