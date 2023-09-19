package x590.jmessenger.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Blob;

@Entity
@Table(name = "images")
@Getter @Setter @ToString
@NoArgsConstructor
public class Image {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
	private Message message;

	@NotNull
	private Blob data;

	public Image(@NotNull Blob data, Message message) {
		this.data = data;
		this.message = message;
	}
}
