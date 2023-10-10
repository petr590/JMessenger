package x590.jmessenger.controllers.user;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import x590.jmessenger.service.UserService;

import java.io.IOException;
import java.sql.SQLException;

import static x590.jmessenger.util.Util.*;

@Controller
@RequestMapping("/users")
public class UsersController {

	private final UserService userService;

	@Autowired
	public UsersController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public String users(Model model) {
		model.addAttribute("users", userService.getUsers());
		return "users/index";
	}

	@GetMapping("/{id:\\d+}")
	public String user(@PathVariable("id") int id, Model model) {
		model.addAttribute("user", userService.findUserById(id));
		return "users/user";
	}


	@GetMapping("/{id:\\d+}/picture")
	public void picture(@PathVariable("id") int id, HttpServletResponse response) throws SQLException, IOException {
		writePictureOrRedirect(response, userService.findUserById(id).getPicture(), "/image/default-picture/user.svg");
	}
}
