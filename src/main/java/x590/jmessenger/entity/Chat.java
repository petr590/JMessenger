package x590.jmessenger.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SortComparator;
import org.springframework.security.access.AccessDeniedException;
import x590.jmessenger.util.Util;

import java.sql.Blob;
import java.util.HashSet;
import java.util.Set;

import static x590.jmessenger.util.Util.copyFieldIfNotNull;

@Entity
@Table(name = "chats")
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Chat implements EntityWithPicture {

	@JsonProperty
	@EqualsAndHashCode.Include
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;


	public static final int
			MIN_TITLE_LENGTH = 6,
			MAX_TITLE_LENGTH = 255;

	@JsonProperty
	@NotNull(message = "{error.chat.titleNotSpecified}")
//	@NotBlank(message = "{error.chat.titleCannotBeBlank}", groups = ChatValidation.class)
	@Size(min = MIN_TITLE_LENGTH, max = MAX_TITLE_LENGTH,
			message = "{error.chat.invalidTitleLength}", groups = Update.class)
	private String title;

	private String description;


	@ManyToOne
	private User owner;


	@NotNull(message = "{error.chat.accessNotSpecified}")
	@Enumerated(EnumType.STRING)
	private Access access;


	@ToString.Exclude
	@ManyToMany
	@JoinTable(
			name = "chats_members",
			joinColumns = @JoinColumn(name = "chat_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private Set<User> members = new HashSet<>();

	@ToString.Exclude
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "chat")
	@SortComparator(Message.MessageComparator.class)
	private Set<Message> messages;

	private Blob picture;

	@Transactional
	public Set<Message> getMessages() {
		return messages;
	}

	public void checkOwner(Util util, User user) {
		if (!owner.equals(user)) {
			throw new AccessDeniedException(
					util.getMessage("error.user.isNotOwnerOfChat", user.getId(), user.getUsername(), id, title)
			);
		}
	}

	public void checkPermissions(Util util, User user) {
		if (!owner.equals(user) && !getMembers().contains(user)) {
			throw new AccessDeniedException(
					util.getMessage("error.user.hasNoAccessToChat", user.getId(), user.getUsername(), id, title)
			);
		}
	}

	/**
	 * Обновляет поля {@link #title}, {@link #description} и {@link #picture}
	 * с переданного объекта, если они не равны {@code null}
	 */
	public void update(Chat other) {
		copyFieldIfNotNull(this::setTitle,       other::getTitle);
		copyFieldIfNotNull(this::setDescription, other::getDescription);
		copyFieldIfNotNull(this::setPicture,     other::getPicture);
	}


	public interface Update {}


	public enum Access {
		PUBLIC, PROTECTED
	}
}
