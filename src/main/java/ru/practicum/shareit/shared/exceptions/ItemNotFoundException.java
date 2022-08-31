package ru.practicum.shareit.shared.exceptions;


public class ItemNotFoundException extends RuntimeException {
	public ItemNotFoundException(String message) {
		super(message);
	}
}
