package x590.jmessenger.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import x590.jmessenger.exception.BadRequestException;
import x590.jmessenger.util.Util;

import java.sql.Timestamp;
import java.util.Comparator;

@Entity
@Table(name = "messages")
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Message implements Comparable<Message> {

	@JsonProperty
	@EqualsAndHashCode.Include
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private Chat chat;

	@ManyToOne
	private User author;


	@JsonProperty
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Timestamp publicationTime;

	@JsonProperty
	private String text;


	@ManyToOne
	private @Nullable Message replyFor;

	@JsonProperty
	@JsonInclude(Include.NON_NULL)
	@Embedded
	private File file;


	@Transient
	private @Nullable Timestamp lastEditTime;


	@JsonGetter
	public int getAuthorId() {
		return author.getId();
	}

	@JsonProperty("replyFor")
	@JsonInclude(Include.NON_DEFAULT)
	public int getReplyForId() {
		var replyFor = this.replyFor;
		return replyFor == null ? 0 : replyFor.id;
	}

	public void checkPermissions(Util util, User user) {
		chat.checkPermissions(util, user);

		if (author.getId() != user.getId()) {
			throw new AccessDeniedException(
					String.format("User #%d \"%s\" has no access to edit message #%d", user.getId(), user.getUsername(), id)
			);
		}
	}

	@Override
	public int compareTo(Message other) {
		return publicationTime.compareTo(other.publicationTime);
	}

	public Message checkIsIn(Chat chat) {
		if (chat.getId() != this.chat.getId()) {
			throw new BadRequestException(
					String.format("Message #%d is not in the chat #%d(%s)", id, chat.getId(), chat.getTitle())
			);
		}

		return this;
	}

	public static final class MessageComparator implements Comparator<Message> {
		@Override
		public int compare(Message m1, Message m2) {
			return m1.compareTo(m2);
		}
	}
}
