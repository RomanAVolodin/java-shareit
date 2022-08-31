package ru.practicum.shareit.user.storage.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UsersStorageDao {

	Collection<User> getAll();

	Optional<User> findById(Long id);

	Optional<User> findByEmail(String email);

	User create(User user);

	User update(User user);

	Optional<User> findByEmailExceptId(String email, Long id);

	void remove(Long id);
}
