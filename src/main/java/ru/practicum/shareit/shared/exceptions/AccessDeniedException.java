package ru.practicum.shareit.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccessDeniedException extends ResponseStatusException {
	public AccessDeniedException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}
