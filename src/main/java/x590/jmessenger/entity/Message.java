package x590.jmessenger.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter @Setter @ToString
@NoArgsConstructor
public class Message implements Comparable<Message> {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
	private User author;

	private Timestamp publicationTime;

	private String text;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "message_id")
	private List<Image> images;

	@OneToOne
	private @Nullable Message answerFor;


	@Override
	public int compareTo(Message other) {
		return publicationTime.compareTo(other.publicationTime);
	}

	public static final class MessageComparator implements Comparator<Message> {
		@Override
		public int compare(Message m1, Message m2) {
			return m1.compareTo(m2);
		}
	}
}
