package ru.practicum.shareit.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityExistsException extends ResponseStatusException {
	public EntityExistsException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
