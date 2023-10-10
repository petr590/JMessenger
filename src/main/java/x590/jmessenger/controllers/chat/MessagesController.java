package x590.jmessenger.controllers.chat;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.File;
import x590.jmessenger.entity.Message;
import x590.jmessenger.entity.User;
import x590.jmessenger.service.ChatService;
import x590.jmessenger.service.MessageService;
import x590.jmessenger.util.Util;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static x590.jmessenger.util.Util.*;

@RestController
@AllArgsConstructor
public class MessagesController {

	private final ChatService chatService;
	private final MessageService messageService;
	private final Util util;

	private final Int2ObjectMap<Timestamp> deleted = new Int2ObjectArrayMap<>();

	private final Int2ObjectMap<Message> edited = new Int2ObjectArrayMap<>();

	@GetMapping("/messages/files/{id:\\d+}")
	public void file(@PathVariable("id") int id, HttpServletResponse response) throws SQLException, IOException {
		var file = messageService.findMessageById(id).getFile();

		if (file == null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}

		writeBlobToResponse(response, file.getContentType(), file.getData());
	}

	@PostMapping("/chats/{chatId:\\d+}/message")
	public ResponseEntity<String> newMessage(
			@PathVariable("chatId") int chatId,
			@RequestPart(name = "text", required = false) String text,
			@RequestPart(name = "file", required = false) MultipartFile multipartFile,
			@RequestParam(name = "replyFor", required = false) Integer replyFor
	) throws SQLException, IOException {

		var publicationTime = new Timestamp(System.currentTimeMillis());
		var user = getAuthenticatedUser();
		var chat = chatService.findChatById(chatId);

		chat.checkPermissions(util, user);

		var message = Message.builder()
				.chat(chat)
				.author(user)
				.text(ObjectUtils.defaultIfNull(text, ""))
				.publicationTime(publicationTime)
				.replyFor(replyFor != null ? messageService.findMessageById(replyFor).checkIsIn(chat) : null)
				.file(multipartFile != null ? new File(multipartFile) : null)
				.build();

		if (messageService.saveMessage(message)) {
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.badRequest().build();
	}

	@PatchMapping("/messages/{messageId:\\d+}")
	public void edit(@PathVariable("messageId") int messageId, @RequestBody String text) {
		var message = messageService.findMessageById(messageId);
		message.checkPermissions(util, getAuthenticatedUser());

		messageService.updateMessage(message, text);
		message.setLastEditTime(new Timestamp(System.currentTimeMillis()));
		edited.put(messageId, message);
	}

	@DeleteMapping("/messages/{messageId:\\d+}")
	public void delete(@PathVariable("messageId") int messageId) {
		var message = messageService.findMessageById(messageId);
		message.checkPermissions(util, getAuthenticatedUser());

		messageService.delete(message);
		deleted.put(messageId, new Timestamp(System.currentTimeMillis()));
		edited.remove(message.getId());
	}

	@GetMapping(value = "/chats/{chatId:\\d+}/changes", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> changes(
			@PathVariable("chatId") int chatId,
			@RequestParam("since") Timestamp since,
			HttpServletRequest request
	) {

		var messages = messageService.findMessagesInChatSince(chatId, since);

		var users = messages.parallelStream().unordered()
				.map(Message::getAuthor).distinct()
				.collect(Collectors.toMap(User::getId, User::getUsername));

		if (request.getParameterMap().containsKey("include-edited-and-deleted")) {
			var editedIter = edited.int2ObjectEntrySet().parallelStream().unordered().map(Map.Entry::getValue)
					.filter(entry -> Objects.requireNonNull(entry.getLastEditTime()).compareTo(since) >= 0)
					.iterator();

			var deletedIter = deleted.int2ObjectEntrySet().parallelStream().unordered()
					.filter(entry -> entry.getValue().compareTo(since) >= 0)
					.mapToInt(Int2ObjectMap.Entry::getIntKey)
					.iterator();

			return Map.of(
					"messages", messages,
					"usernames", users,
					"currentUserId", getAuthenticatedUser().getId(),
					"edited", editedIter,
					"deleted", deletedIter
			);
		}

		return Map.of(
				"messages", messages,
				"usernames", users,
				"currentUserId", getAuthenticatedUser().getId()
		);
	}
}
