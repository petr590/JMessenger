package x590.jmessenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import x590.jmessenger.entities.User;
import x590.jmessenger.service.ChatService;
import x590.jmessenger.service.UserService;

@Controller
public class PagesController {

	private final UserService userService;
	private final ChatService chatService;

	@Autowired
	public PagesController(UserService userService, ChatService chatService) {
		this.userService = userService;
		this.chatService = chatService;
	}

	@GetMapping("/user/{userId}")
	public String getUser(Model model, @PathVariable int userId) {
		model.addAttribute("user", userService.findById(userId).orElse(null));
		return "user.html";
	}

	@PostMapping(value = "/user/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateUser(@PathVariable int userId, @RequestBody User user) {
		return userService.updateById(userId, user) ?
				ResponseEntity.status(HttpStatus.OK).body("") :
				ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
	}


	@GetMapping("/chat/{chatId}")
	public String chat(Model model, @PathVariable int chatId) {
		model.addAttribute("chat", chatService.findById(chatId).orElse(null));
		return "chat.html";
	}
}
