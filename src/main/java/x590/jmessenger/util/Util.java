package x590.jmessenger.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import x590.jmessenger.entity.User;

public final class ControllerUtil {

	private ControllerUtil() {}

	public static User getAuthenticatedUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication.getPrincipal() instanceof User user) {
			return user;
		}

		throw new IllegalStateException("Cannot get user: " +
				authentication.getPrincipal() + " is not instanceof User");
	}
}
