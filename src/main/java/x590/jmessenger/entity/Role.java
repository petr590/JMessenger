package x590.jmessenger.entity;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public enum Role implements GrantedAuthority {
	USER("пользователь"),
	ADMIN("администратор");

	private final String name;

	private final Collection<Role> collection = Collections.singleton(this);

	Role(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String getAuthority() {
		return name();
	}

	public Collection<Role> asCollection() {
		return collection;
	}
}
