package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "date_start")
	private LocalDateTime dateStart;

	@Column(name = "date_end")
	private LocalDateTime dateEnd;

	@Column(name = "item_id")
	private Long itemId;

	@Column(name = "booker_id")
	private Long bookerId;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private BookingStatus status = BookingStatus.WAITING;

}
