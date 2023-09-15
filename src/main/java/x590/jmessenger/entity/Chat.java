package x590.jmessenger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SortComparator;

import java.util.Set;

@Entity
@Table(name = "chats")
@Getter @Setter @ToString
@NoArgsConstructor
public class Chat {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 6, max = 255, message = "Заголовок должен быть не меньше 2 символов и не больше 255")
	private String title;

	private String description;

	@ToString.Exclude
	@ManyToMany(mappedBy = "chats", fetch = FetchType.EAGER)
	private Set<User> users;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "chat_id")
	@SortComparator(Message.MessageComparator.class)
	private Set<Message> messages;
}
