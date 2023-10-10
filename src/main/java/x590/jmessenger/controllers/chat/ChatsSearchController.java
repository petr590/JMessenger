package x590.jmessenger.controllers.chat;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import x590.jmessenger.entity.Chat;
import x590.jmessenger.service.ChatService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ChatsSearchController {

	private final ChatService chatService;

	@GetMapping(value = "/chats/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Chat> search(@RequestParam("value") String value,
	                         @RequestParam("page") int page,
	                         @RequestParam("size") int size) {

		if (value.length() > Chat.MAX_TITLE_LENGTH) {
			return List.of();
		}

		return chatService.searchChats(value, page, size);
	}
}
