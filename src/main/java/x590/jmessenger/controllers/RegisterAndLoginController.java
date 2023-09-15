package x590.jmessenger.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import x590.jmessenger.entity.User;
import x590.jmessenger.service.UserService;

@Controller
public class RegisterAndLoginController {

	private final UserService userService;

	@Autowired
	public RegisterAndLoginController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/register")
	public String register(@ModelAttribute("user") User user) {
		return "user/register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
						   HttpServletRequest request) {

		if (bindingResult.hasErrors() || !userService.saveUser(user, bindingResult)) {
			return "user/register";
		}

		var token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

		SecurityContextHolder.getContext().setAuthentication(token);

		return "redirect:/";
	}


	@GetMapping("/login")
	public String login(@ModelAttribute("user") User user) {
		return "user/login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "user/logout";
	}
}
