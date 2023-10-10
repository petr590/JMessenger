package x590.jmessenger.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import java.sql.Blob;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static x590.jmessenger.util.Util.*;

@Entity
@Table(name = "users")
@Getter @Setter @ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class User implements UserDetails, EntityWithPicture {

	@EqualsAndHashCode.Include
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Size(min = 2, max = 255, message = "{error.user.invalidLoginLength}")
	private String username;

	@Size(min = 6, max = 255, message = "{error.user.invalidPasswordLength}")
	private String password;

	@Transient
	private String repeatedPassword;

	@Enumerated(EnumType.STRING)
	private Role role;

	private Blob picture;

	private boolean deleted;

	@ToString.Exclude
	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private Set<Chat> ownedChats = new HashSet<>();

	@ToString.Exclude
	@ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
	private Set<Chat> chats = new HashSet<>();

	@Transactional
	public Set<Chat> getOwnedChats() {
		return ownedChats;
	}

	@Transactional
	public Set<Chat> getChats() {
		return chats;
	}

	/**
	 * Обновляет поля {@link #username}, {@link #password} и {@link #picture}
	 * с переданного объекта, если они не равны {@code null}
	 */
	public void update(User other) {
		copyFieldIfNotNull(this::setUsername, other::getUsername);
		copyFieldIfNotNull(this::setPassword, other::getPassword);
		copyFieldIfNotNull(this::setPicture,  other::getPicture);
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
			bindingResult.rejectValue("repeatedPassword", "error.user.passwordsDontMatch");
			return false;
		}

		return true;
	}
}
