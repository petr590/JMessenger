package x590.jmessenger.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import x590.jmessenger.exception.BadRequestException;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler({ UsernameNotFoundException.class, EntityNotFoundException.class })
	public String notFound(Exception exception, Model model, HttpServletResponse response) {
		return handleException(exception, model, response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public String accessDenied(Exception exception, Model model, HttpServletResponse response) {
		return handleException(exception, model, response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(BadRequestException.class)
	public String badRequest(Exception exception, Model model, HttpServletResponse response) {
		return handleException(exception, model, response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String maxUploadSizeExceeded(Exception exception, Model model, HttpServletResponse response) {
		return handleException(exception, model, response, HttpStatus.PAYLOAD_TOO_LARGE);
	}

	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class, MissingServletRequestParameterException.class })
	public String missingServletRequestParameter(Exception exception, Model model, HttpServletResponse response) {
		exception.printStackTrace();
		return handleException(exception, model, response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Throwable.class)
	public String anyException(Throwable exception, Model model, HttpServletResponse response) {
		exception.printStackTrace();
		return handleException(exception, model, response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private static String handleException(Throwable exception, Model model ,HttpServletResponse response, HttpStatus status) {
		model.addAttribute("httpStatus", status);
		model.addAttribute("errorMessage", exception.getMessage());
		response.setStatus(status.value());
		return "error";
	}
}
