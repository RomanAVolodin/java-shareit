package ru.practicum.shareit.shared;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Qualifier("memory")
public class BaseIdCountable {
	protected Long counter = 0L;
	public Long getNextId() {
		return ++counter;
	}
}
