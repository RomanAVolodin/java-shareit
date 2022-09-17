package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {

	private final ItemService service;

	@GetMapping
	public List<ItemResponseDto> findAll(
			@RequestHeader("X-Sharer-User-Id") Long ownerId,
			@RequestParam (name="from", defaultValue = "0") @Min(0) int from,
			@RequestParam (name="size", defaultValue = "10") @Min(1) int size
	) {
		return service.getAll(ownerId, from, size);
	}

	@GetMapping("/search")
	public List<ItemResponseDto> searchAvailable(
			@RequestParam(required = true) String text,
			@RequestParam (name="from", defaultValue = "0") @Min(0) int from,
			@RequestParam (name="size", defaultValue = "10") @Min(1) int size
	) {
		return service.searchAvailable(text, from, size);
	}

	@PostMapping
	public ItemResponseDto create(
			@RequestHeader("X-Sharer-User-Id") Long ownerId,
			@Valid @RequestBody ItemCreateDto dto
	) {
		return service.create(dto, ownerId);
	}

	@GetMapping("/{id}")
	public ItemResponseDto findItemById(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable("id") Long itemId
	) {
		return service.findByUserIdAndItemId(userId, itemId);
	}

	@PatchMapping("/{id}")
	public ItemResponseDto updateItemById(
			@RequestHeader("X-Sharer-User-Id") Long ownerId,
			@PathVariable("id") Long id,
			@Valid @RequestBody ItemUpdateDto dto
			) {
		return service.update(id, ownerId, dto);
	}

	@DeleteMapping (value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void remove(
			@PathVariable(name = "id") Long id,
			@RequestHeader("X-Sharer-User-Id") Long ownerId
	) {
		service.delete(id, ownerId);
	}

	@PostMapping("/{itemId}/comment")
	public CommentResponseDto createComment(
			@Valid @RequestBody CommentCreateDto comment,
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable("itemId") Long itemId
	) {
		return service.addComment(userId, itemId, comment);
	}
}
