package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	@Query("select u from User as u" +
			" where u.email = :email " +
			" and u.id <> :id")
	Optional<User> findByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);
}
