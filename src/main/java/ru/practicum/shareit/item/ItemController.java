package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {

	private final ItemService service;

	@GetMapping
	public List<ItemResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
		return service.getAll(ownerId);
	}

	@GetMapping("/search")
	public List<ItemResponseDto> searchAvailable(@RequestParam(required = true) String text) {
		return service.searchAvailable(text);
	}

	@PostMapping
	public ItemResponseDto create(
			@RequestHeader("X-Sharer-User-Id") Long ownerId,
			@Valid @RequestBody ItemCreateDto dto
	) {
		return service.create(dto, ownerId);
	}

	@GetMapping("/{id}")
	public ItemResponseDto findItemById(@PathVariable("id") Long id) {
		return service.getById(id);
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
}
