package x590.jmessenger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import x590.jmessenger.service.ChatService;

import static x590.jmessenger.controllers.ControllerUtil.*;

@Controller
@RequestMapping("/chats")
public class ChatsController {

	private final ChatService chatService;

	@Autowired
	public ChatsController(ChatService chatService) {
		this.chatService = chatService;
	}

	@GetMapping
	public String chats(Model model) {
		model.addAttribute("chats", getAuthenticatedUser().getChats());
		return "chats/index";
	}

	@GetMapping("/{id}")
	public String chat(@PathVariable("id") int id, Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		model.addAttribute("chat", chatService.findChatById(id));
		return "chats/chat";
	}
}
