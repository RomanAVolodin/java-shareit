package ru.practicum.shareit.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> constraintViolation(final ConstraintViolationException ex) {
		return Map.of("error", ex.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> requestParameter(final MissingServletRequestParameterException ex) {
		return Map.of("error", ex.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> missingHeader(final MissingRequestHeaderException ex) {
		return Map.of("error", ex.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> bodyMissed(final HttpMessageNotReadableException ex) {
		return Map.of("error", ex.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> badRequest(final BadRequestException ex) {
		return Map.of("error", ex.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, String> methodArgumentNotValid(final MethodArgumentNotValidException ex) {
		List<String> errors = new ArrayList<String>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		return Map.of("error", String.join(",", errors));
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public Map<String, String> notFoundException(final ItemNotFoundException e) {
		return Map.of("error", Objects.requireNonNull(e.getMessage()));
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public Map<String, String> alreadyExistsException(final EntityExistsException e) {
		return Map.of("error", Objects.requireNonNull(e.getMessage()));
	}
}