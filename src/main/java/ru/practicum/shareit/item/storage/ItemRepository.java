package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable page);

	List<Item> findAllByRequestIdOrderByIdAsc(Long requestId);

	@Query("select it from Item as it" +
			" where (upper(it.name) like concat('%', upper(:text), '%' ) " +
			" or upper(it.description) like concat('%', upper(:text), '%' ))" +
			" and it.available = true order by it.id asc")
	List<Item> searchAvailable(@Param("text") String text, Pageable page);
}
