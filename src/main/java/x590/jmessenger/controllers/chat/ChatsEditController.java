package x590.jmessenger.controllers.chat;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.Chat;
import x590.jmessenger.service.ChatService;
import x590.jmessenger.util.Util;

import java.io.IOException;
import java.sql.SQLException;

import static x590.jmessenger.util.Util.getAuthenticatedUser;

@Controller
@RequestMapping("/chats")
@AllArgsConstructor
public class ChatsEditController {

	private final ChatService chatService;
	private final Util util;

	private static final String URL_PATTERN = "/{id:\\d+}/{field:title|description}";
	private static final String
			BASE_PATH = "chats/chat/",
			FORM_PATH = BASE_PATH + "form/";

	@GetMapping(URL_PATTERN)
	public String field(@PathVariable("id") int id, @PathVariable("field") String field, Model model) {
		return get(id, BASE_PATH, field, model);
	}

	@GetMapping(URL_PATTERN + "/edit")
	public String form(@PathVariable("id") int id, @PathVariable("field") String field, Model model) {
		return get(id, FORM_PATH, field, model);
	}

	private String get(int id, String path, String filename, Model model) {
		model.addAttribute("chat", chatService.findChatById(id));
		return path + filename;
	}


	@PatchMapping(URL_PATTERN)
	public String edit(Model model, HttpServletResponse response,
	                   @PathVariable("id") int id, @PathVariable("field") String field,
	                   @ModelAttribute("chat") @Validated(Chat.Update.class) Chat newChatData,
	                   BindingResult bindingResult) throws SQLException, IOException {

		edit(id, model, response, newChatData, bindingResult, null);

		if (!HttpStatus.valueOf(response.getStatus()).is2xxSuccessful()) {
			return FORM_PATH + field;
		}

		model.addAttribute("chat", chatService.findChatById(id));
		return BASE_PATH + field;
	}


	@PatchMapping("/{id:\\d+}")
	public String edit(
			@PathVariable("id") int id, Model model, HttpServletResponse response,
			@ModelAttribute("chat") @Validated(Chat.Update.class) Chat newChatData,
			BindingResult bindingResult,
			@RequestPart(name = "pictureFile", required = false) MultipartFile picture
	) throws SQLException, IOException {

		var chat = chatService.findChatById(id);
		chat.checkOwner(util, getAuthenticatedUser());

		util.setPictureIfNotEmpty(picture, chat, bindingResult);

		if (!bindingResult.hasErrors()) {
			chatService.updateChat(chat, newChatData, bindingResult);

			if (!bindingResult.hasErrors()) {
				return "empty";
			}
		}

		response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
		return FORM_PATH + "errors";
	}
}
