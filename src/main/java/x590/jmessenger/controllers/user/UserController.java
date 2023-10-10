package x590.jmessenger.controllers.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import x590.jmessenger.entity.User;
import x590.jmessenger.service.UserService;
import x590.jmessenger.util.Util;

import java.io.IOException;
import java.sql.SQLException;

import static x590.jmessenger.util.Util.*;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

	private final UserService userService;
	private final Util util;

	@GetMapping
	public String user(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/index";
	}


	@PatchMapping
	public String edit(@ModelAttribute("user") @Valid User newUserData, BindingResult bindingResult,
	                   @RequestPart(name = "pictureFile", required = false) MultipartFile picture,
	                   Model model) throws SQLException, IOException {

		var user = getAuthenticatedUser();
		util.setPictureIfNotEmpty(picture, user, bindingResult);

		if (!bindingResult.hasErrors()) {
			userService.updateUser(user, newUserData, bindingResult);
		}

		// Необходимо для правильного отображения профиля
		newUserData.setId(user.getId());
		newUserData.setRole(user.getRole());

		return "user/index";
	}


	@GetMapping("/new-password")
	public String newPassword(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/new-password";
	}

	@PatchMapping("/new-password")
	public String newPassword(@ModelAttribute("user") @Valid User newUserData,
	                          BindingResult bindingResult, Model model) {

		var user = getAuthenticatedUser();

		newUserData.setUsername(null);
		newUserData.setPicture(null);

		if (bindingResult.hasErrors() || !newUserData.checkPasswords(bindingResult) ||
			!userService.updateUser(user, newUserData, bindingResult)) {
			return "user/new-password";
		}

		return "user/edit";
	}


	@GetMapping("/delete")
	public String getDeletePage(Model model) {
		model.addAttribute("user", getAuthenticatedUser());
		return "user/delete";
	}

	@DeleteMapping
	public String delete(HttpServletRequest request) throws ServletException {
		userService.deleteUser(getAuthenticatedUser());
		request.logout();
		return "redirect:/";
	}
}
