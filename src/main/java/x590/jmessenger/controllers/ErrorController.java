package x590.jmessenger.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorController {

	@ExceptionHandler({UsernameNotFoundException.class, EntityNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String usernameNotFoundException(RuntimeException exception, Model model) {
		model.addAttribute("httpStatus", HttpStatus.NOT_FOUND);
		model.addAttribute("errorMessage", exception.getMessage());
		return "error.html";
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String exception(Throwable exception, Model model) {
		model.addAttribute("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);
		model.addAttribute("errorMessage", exception.getMessage());
		return "error.html";
	}
}
