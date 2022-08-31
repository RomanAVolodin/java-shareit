package ru.practicum.shareit.shared.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends RuntimeException {
	public ItemNotFoundException(String message) {
		super(message);
	}
}
