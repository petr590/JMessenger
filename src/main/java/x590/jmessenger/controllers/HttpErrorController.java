package x590.jmessenger.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/error")
@AllArgsConstructor
public class HttpErrorController implements ErrorController {

	@GetMapping
	public String error(HttpServletRequest request, Model model) {
		int code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
				instanceof Integer intStatus ? intStatus : 0;

		model.addAttribute("httpStatus", HttpStatus.resolve(code));
		return "error";
	}

	@GetMapping("/403")
	public String forbidden(Model model) {
		model.addAttribute("httpStatus", HttpStatus.FORBIDDEN);
		model.addAttribute("messageKey", "httpError.message.accessDenied");
		return "error";
	}
}
