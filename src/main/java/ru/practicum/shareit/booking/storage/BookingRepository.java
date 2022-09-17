package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findAllByBookerIdOrderByDateEndDesc(Long bookedId);

	List<Booking> findAllByBookerIdAndStatusOrderByDateEndDesc(Long bookerId, BookingStatus status);

	@Query("select b from Booking as b" +
			" join Item as i on i.id = b.itemId" +
			" where i.ownerId = :ownerId" +
			" order by b.dateEnd desc ")
	List<Booking> findAllByOwnerIdOrderByEndDesc(@Param("ownerId") Long ownerId);

	@Query("select b from Booking as b" +
			" join Item as i on i.id = b.itemId" +
			" where i.ownerId = :ownerId and b.status = :status" +
			" order by b.dateEnd desc ")
	List<Booking> findAllByOwnerIdAndStatusOrderByEndDesc(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

	@Query(value = "SELECT * from Bookings b " +
			" JOIN Items i ON i.id = b.item_id" +
			" WHERE i.id = :itemId AND i.owner_id =:ownerId AND b.status = 'APPROVED'" +
			" ORDER BY b.date_end " +
			" LIMIT 2",
			nativeQuery = true)
	List<Booking> findTwoBookingByOwnerIdOrderByEndAsc(@Param("ownerId") Long ownerId, @Param("itemId") Long itemId);

	List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

}
