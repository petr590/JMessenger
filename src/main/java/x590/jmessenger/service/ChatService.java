package x590.jmessenger.service;

import x590.jmessenger.entity.Chat;

public interface ChatService {

	Chat findChatById(int id);

//	List<Chat> getChats();
//
//	boolean saveChat(Chat chat, BindingResult bindingResult);
//
//	void updateChat(Chat existingChat, Chat newChatData);

	boolean deleteChat(int id);
}
