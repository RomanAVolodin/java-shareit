package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

	private final ItemRequestService itemRequestService;

	@PostMapping
	public ItemRequestResponseDto create(
			@RequestHeader("X-Sharer-User-Id") Long requesterId,
			@Valid @RequestBody ItemRequestCreateDto itemRequestDto)
	{
		return itemRequestService.create(itemRequestDto, requesterId);
	}

	@GetMapping
	public List<ItemRequestResponseDto> getRequestList(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
		return itemRequestService.getRequestListByRequester(requesterId);
	}

	@GetMapping("/all")
	public List<ItemRequestResponseDto> getRequestListByPages(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam (name="from", defaultValue = "0") @Min(0) Integer from,
			@RequestParam (name="size", defaultValue = "10") @Min(1) Integer size)
	{
		return itemRequestService.getRequestListByPages(userId, from, size);
	}

	@GetMapping("{requestId}")
	public ItemRequestResponseDto getRequestById(
			@RequestHeader("X-Sharer-User-Id") Long userId,
			@PathVariable long requestId)
	{
		return itemRequestService.getRequestByID(userId, requestId);
	}
}
