package x590.jmessenger.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Entity
@Table(name = "users")
@Getter @Setter @ToString
@NoArgsConstructor
public class User implements UserDetails {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 2, max = 255, message = "Имя должно быть не меньше 2 символов и не больше 255")
	private String username;

	@Size(min = 6, max = 255, message = "Пароль должен быть не меньше 6 символов и не больше 255")
	private String password;

	@Transient
	private String repeatedPassword;

	@Enumerated(EnumType.STRING)
	private Role role;


	@ToString.Exclude
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "chats_members",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "chat_id")
	)
	private Set<Chat> chats;


	/**
	 * Обновляет поля {@link #username} и {@link #password} с переданного объекта,
	 * если они не равны {@code null}
	 */
	public void update(User other) {
		copyFieldIfNotNull(this::setUsername, other::getUsername);
		copyFieldIfNotNull(this::setPassword, other::getPassword);
	}

	private <T> void copyFieldIfNotNull(Consumer<T> setter, Supplier<T> getter) {
		T value = getter.get();
		if (value != null) {
			setter.accept(value);
		}
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.asCollection();
	}

	public void encodePassword(PasswordEncoder encoder) {
		var password = this.password;

		if (password != null) {
			this.password = encoder.encode(password);
		}
	}

	public boolean checkPasswords(BindingResult bindingResult) {
		if (password == null || !password.equals(repeatedPassword)) {
			bindingResult.addError(new ObjectError("repeatedPassword", "Пароли не совпадают"));
			return false;
		}

		return true;
	}
}
