package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "available")
	private Boolean available;

	@Column(name = "owner_id")
	private Long ownerId;

	@Column(name = "request_id")
	private Long requestId;

	@Transient
	private Booking lastBooking;

	@Transient
	private Booking nextBooking;

	@Transient
	@Builder.Default
	private List<Comment> comments = new ArrayList<>();
}
