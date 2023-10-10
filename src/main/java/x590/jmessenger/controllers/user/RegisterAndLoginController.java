package x590.jmessenger.controllers.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.User;
import x590.jmessenger.service.UserService;

@Controller
@AllArgsConstructor
public class RegisterAndLoginController {

	private final UserService userService;

	@GetMapping("/register")
	public String register(@ModelAttribute("user") User user) {
		return "user/register";
	}

	@PostMapping("/register")
	public String register(
			@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
			HttpServletRequest request) throws ServletException {

		// Необходимо запомнить пароль до сохранения пользователя,
		// так как при сохранении пароль кодируется
		var password = user.getPassword();

		if (bindingResult.hasErrors() || !userService.saveUser(user, bindingResult)) {
			return "user/register";
		}

		request.login(user.getUsername(), password);

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
