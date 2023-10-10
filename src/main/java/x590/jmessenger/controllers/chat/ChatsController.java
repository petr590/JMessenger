package x590.jmessenger.controllers.chat;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.Chat;
import x590.jmessenger.entity.User;
import x590.jmessenger.service.ChatService;
import x590.jmessenger.service.UserService;
import x590.jmessenger.util.Util;

import java.io.IOException;
import java.sql.SQLException;

import static x590.jmessenger.util.Util.*;

@Controller
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatsController {

	private final ChatService chatService;
	private final UserService userService;
	private final SessionRegistry sessionRegistry;
	private final Util util;

	@SuppressWarnings("unused")
	@Value("${spring.servlet.multipart.max-file-size}")
	private DataSize maxFileSize;

	@GetMapping
	public String chats(Model model) {
		model.addAttribute("chats", getAuthenticatedUser().getChats());
		return "chats/index";
	}

	@GetMapping("/new")
	public String newChat(@ModelAttribute("chat") Chat chat) {
		return "chats/new";
	}

	@PostMapping("/new")
	public String newChat(@ModelAttribute("chat") @Valid Chat chat, BindingResult bindingResult,
						  @RequestPart(name = "pictureFile", required = false) MultipartFile picture,
	                      Model model) throws SQLException, IOException {

		var user = getAuthenticatedUser();

		chat.setOwner(user);
		chat.getMembers().add(user);

		if (bindingResult.hasErrors() || !chatService.saveChat(chat, picture, bindingResult)) {
			return "chats/new";
		}

		user.getChats().add(chat);

		return "redirect:/chats/" + chat.getId();
	}

	@GetMapping("/{id:\\d+}")
	public String chat(@PathVariable("id") int id, Model model) {
		var user = getAuthenticatedUser();
		var chat = chatService.findChatById(id);

		model.addAttribute("user", user);
		model.addAttribute("chat", chat);

		if (chat.getMembers().contains(user)) {
			model.addAttribute("maxFileSize", maxFileSize);
			return "chats/chat";
		}

		return"chats/join";
	}

	@GetMapping("/{id:\\d+}/picture")
	public void picture(@PathVariable("id") int id, HttpServletResponse response) throws SQLException, IOException {
		writePictureOrRedirect(response, chatService.findChatById(id).getPicture(), "/image/default-picture/chat.svg");
	}

	@PostMapping("/{id:\\d+}/join")
	public String join(@PathVariable("id") int id) {
		var user = getAuthenticatedUser();
		var chat = chatService.findChatById(id);

		chat.getMembers().add(user);
		user.getChats().add(chat);
		chatService.updateChat(chat);

		return "redirect:/chats/" + id;
	}

	@PostMapping("/{id:\\d+}/leave")
	public String leave(@PathVariable("id") int id) {
		var user = getAuthenticatedUser();
		var chat = chatService.findChatById(id);

		if (!user.equals(chat.getOwner())) {
			chat.getMembers().remove(user);
			user.getChats().remove(chat);
			chatService.updateChat(chat);
		}

		return "redirect:/chats";
	}

	@DeleteMapping("/{id:\\d+}")
	public String delete(@PathVariable("id") int id) {
		var chat = chatService.findChatById(id);
		var owner = getAuthenticatedUser();

		chat.checkOwner(util, owner);

		var principals = sessionRegistry.getAllPrincipals();
		var members = chat.getMembers();

		for (var principal : principals) {
			if (principal instanceof User user && members.contains(user)) {
				members.remove(user);
				user.getChats().remove(chat);

				if (user.equals(owner)) {
					user.getOwnedChats().remove(chat);
				}

				userService.updateUser(user);
			}
		}

		chatService.deleteChat(chat);
		return "redirect:/chats";
	}
}
