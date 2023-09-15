package x590.jmessenger.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import x590.jmessenger.entity.User;
import x590.jmessenger.service.UserService;

import static x590.jmessenger.controllers.ControllerUtil.*;

@Controller
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public String user(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/index";
	}


	@GetMapping("/edit")
	public String edit(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/edit";
	}

	@PatchMapping("/edit")
	public String update(@ModelAttribute("user") @Valid User newUserData, BindingResult bindingResult,
						 Model model) {

		if (bindingResult.hasErrors()) {
			return "user/edit";
		}

		var user = getAuthenticatedUser();

		userService.updateUser(user, newUserData);

		model.addAttribute("user", user);
		return "redirect:/user";
	}


	@GetMapping("/new-password")
	public String newPassword(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/new-password";
	}

	@PatchMapping("/new-password")
	public String newPassword(@ModelAttribute("user") @Valid User newUserData, BindingResult bindingResult,
							  Model model) {

		if (bindingResult.hasErrors() || !newUserData.checkPasswords(bindingResult)) {
			return "user/new-password";
		}

		var user = getAuthenticatedUser();

		userService.updateUser(user, newUserData);

		model.addAttribute("user", user);
		return "user/edit";
	}


	@GetMapping("/delete")
	public String getDeletePage(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/delete";
	}

	@DeleteMapping
	public String delete() {
		userService.deleteUser(getAuthenticatedUser());
		return "redirect:/";
	}
}
