package x590.jmessenger.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public final class User {
	@Id
	@GeneratedValue(generator = "increment")
	private int id;

	private String name;

	private User() {}

	public User(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
