package ru.practicum.shareit.user.storage.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.dao.UsersStorageDao;

import java.util.*;


@Repository
@Qualifier("memory")
public class UsersInMemoryStorage implements UsersStorageDao {

	private final HashMap<Long, User> users = new HashMap<>();

	@Override
	public List<User> getAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public Optional<User> findById(Long id) {
		return Optional.ofNullable(users.get(id));
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return users.values().stream().filter(u -> u.getEmail().equals(email)).findFirst();
	}

	@Override
	public User create(User user) {
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public User update(User user) {
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public Optional<User> findByEmailExceptId(String email, Long id) {
		return users.values().stream().filter(u -> u.getEmail().equals(email) && !u.getId().equals(id)).findFirst();
	}

	@Override
	public void remove(Long id) {
		users.remove(id);
	}
}
