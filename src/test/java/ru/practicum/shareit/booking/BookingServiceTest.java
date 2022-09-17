package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoNames;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.ArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.BookerDto;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.Status.*;

@ExtendWith(MockitoExtension.class)
@Import(ValidationAutoConfiguration.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl service;

    BookingDtoNames bookingDtoNames;
    BookingDto bookingDto;


    Booking bookingPast;
    Booking bookingPresent;
    Booking bookingFuture;
    Booking bookingRejected;

    Pageable pageable = new OffsetBasedPageRequest(20, 0, Sort.by(Sort.Direction.DESC, "id"));

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        mapper.findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        bookingDtoNames = new BookingDtoNames(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookerDto bookerDto = new BookerDto(5L);
        ItemDtoShort itemDtoShort = new ItemDtoShort(7L, "hummer");
        bookingDtoNames.setId(1L);
        bookingDtoNames.setBooker(bookerDto);
        bookingDtoNames.setItem(itemDtoShort);
        bookingDtoNames.setStatus(WAITING);

        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 5L);
        bookingDto.setItemId(2L);

//        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
//        LocalDateTime afterTomorow = LocalDateTime.now().plusDays(2);
//        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

        bookingPast = new Booking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                1L);
        bookingPresent = new Booking(LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(1),
                1L);
        bookingFuture = new Booking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3),
                1L);
        bookingRejected = new Booking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3),
                1L);



        bookingPast.setStatus(APPROVED);
        bookingPast.setId(1L);
        bookingPresent.setStatus(APPROVED);
        bookingPresent.setId(2L);
        bookingFuture.setStatus(WAITING);
        bookingFuture.setId(3L);
        bookingRejected.setStatus(REJECTED);
        bookingRejected.setId(4L);

    }

    @Test
    void checkNotOwnerAndAvailable() {
        when(itemService.checkAvailable(bookingDto.getItemId())).thenReturn(false);
        when(itemService.checkOwnership(bookingDto.getItemId(), 1L)).thenReturn(true);
        Assertions.assertThrows(NotFoundException.class, () -> service.checkNotOwnerAndAvailable(bookingDto,
                1L));
    }

    @Test
    void create() {
        when(itemService.getItemById(anyLong())).thenReturn(new ItemDto("Hummer", "good hummer",
                true));
        when(itemService.checkAvailable(bookingDto.getItemId())).thenReturn(false);
        when(itemService.checkOwnership(bookingDto.getItemId(), 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        BookingDtoNames bookingDtoNames1 = service.create(bookingDto, 1L);
        Assertions.assertTrue(service.create(bookingDto, 1L) instanceof BookingDtoNames);
        Assertions.assertEquals("Hummer", bookingDtoNames1.getItem().getName());
    }

    @Test
    void updateStatus() {
        Booking booking = new Booking(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 1L);
        booking.setId(1L);
        booking.setStatus(WAITING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemService.getOwnerByItemId(1L)).thenReturn(1L);
        when(bookingRepository.findById(1l)).thenReturn(Optional.of(booking));
        when(itemService.checkOwnership(1L, 1L)).thenReturn(true);
        when(bookingRepository.getReferenceById(1L)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingRepository.getReferenceById(1L)).thenReturn(booking);
        when(itemService.getItemById(anyLong())).thenReturn(new ItemDto("Hummer", "good hummer",
                true));

        Assertions.assertEquals(APPROVED, service.updateStatus(1L, true, 1L).getStatus());
    }

    @Test
    void checkExist() {
        Assertions.assertThrows(NotFoundException.class, () -> service.checkExist(1L));
    }

    @Test
    void getById() {
        Booking booking = new Booking(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), 1L);
        booking.setId(1L);
        booking.setStatus(WAITING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.getReferenceById(anyLong())).thenReturn(booking);

        when(itemService.getOwnerByItemId(1L)).thenReturn(1L);
        when(itemService.getItemById(anyLong())).thenReturn(new ItemDto("Hummer", "good hummer",
                true));
        Assertions.assertTrue(service.getById(1L, 1L) instanceof BookingDtoNames);
        Assertions.assertEquals(1, service.getById(1L, 1L).getId());
        Assertions.assertEquals("Hummer", service.getById(1L, 1L).getItem().getName());
    }

    @Test
    void checkInputStateAndGetForOwnerByStateThrowException() {
        Assertions.assertThrows(ArgumentException.class,
                () -> service.checkInputStateAndGetForOwnerByState("SOMETHING", 1L, 0, 20));
    }

    @Test
    void checkInputStateAndGetForBookingUserByStateThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Assertions.assertThrows(ArgumentException.class,
                () -> service.checkInputStateAndGetForBookingUserByState("SOMETHING", 1L, 0, 20));
    }

    @Test
    void getForOwnerByState() {

        when(itemRepository.getItemByOwner(1L)).thenReturn(Arrays.asList(new Item()));

        when(bookingRepository.findBookingByOwnerAndStatePast(anyLong(), any(), any())).thenReturn(Arrays.asList(bookingPast));
        when(itemService.getItemById(anyLong())).thenReturn(new ItemDto("Torch", "bright torch",
                true));
        when(bookingRepository.findBookingByOwnerAndStateFuture(anyLong(), any(), any())).thenReturn(Arrays.asList(bookingFuture));
        when(bookingRepository.findBookingByOwnerAndStateCurrent(anyLong(), any(), any())).thenReturn(Arrays.asList(bookingPresent));
        when(bookingRepository.findBookingByOwnerAndState(1L, REJECTED, pageable)).thenReturn(Arrays.asList(bookingRejected));
        when(bookingRepository.findBookingByOwnerAndState(1L, WAITING, pageable)).thenReturn(Arrays.asList(bookingFuture));
        List count = Arrays.asList(bookingFuture, bookingPast, bookingPresent);
        when(bookingRepository.findBookingByOwnerAndNotRejected(anyLong(), any()))
                .thenReturn(count);

        Assertions.assertEquals(1L, service.getForOwnerByState(1L, State.PAST, 0, 20).get(0).getId());
        Assertions.assertEquals(2L, service.getForOwnerByState(1L, State.CURRENT, 0, 20).get(0).getId());
        Assertions.assertEquals(3L, service.getForOwnerByState(1L, State.FUTURE, 0, 20).get(0).getId());
        Assertions.assertEquals(3L, service.getForOwnerByState(1L, State.WAITING, 0, 20).get(0).getId());
        Assertions.assertEquals(4L, service.getForOwnerByState(1L, State.REJECTED, 0, 20).get(0).getId());
        Assertions.assertEquals(3, service.getForOwnerByState(1L, State.ALL, 0, 20).size());
    }

    @Test
    void getByState() {
        when(itemService.getItemById(anyLong())).thenReturn(new ItemDto("Torch", "bright torch",
                true));
        when(bookingRepository.findByBookerOrderByIdDesc(anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByBookerAndEndBeforeOrderByIdDesc(anyLong(), any(), any())).thenReturn(Arrays.asList(bookingPast));
        when(bookingRepository.findByBookerAndStartAfterOrderByIdDesc(anyLong(), any(), any())).thenReturn(Arrays.asList(bookingFuture));
        when(bookingRepository.findByBookerAndStartBeforeAndEndAfterOrderByIdDesc(anyLong(), any(), any(),any())).thenReturn(Arrays.asList(bookingPresent));
        when(bookingRepository.findByBookerAndStatus(1L, WAITING, pageable)).thenReturn(Arrays.asList(bookingFuture));
        when(bookingRepository.findByBookerAndStatus(1L, REJECTED, pageable)).thenReturn(Arrays.asList(bookingRejected));
        when(bookingRepository.findByBookerOrderByIdDescPagable(anyLong(),any()))
                .thenReturn(Arrays.asList(bookingFuture, bookingPast, bookingPresent));
        Assertions.assertEquals(1L, service.getByState(1L, State.PAST, 0, 20).get(0).getId());
        Assertions.assertEquals(2L, service.getByState(1L, State.CURRENT, 0, 20).get(0).getId());
        Assertions.assertEquals(3L, service.getByState(1L, State.FUTURE, 0, 20).get(0).getId());
        Assertions.assertEquals(3L, service.getByState(1L, State.WAITING, 0, 20).get(0).getId());
        Assertions.assertEquals(4L, service.getByState(1L, State.REJECTED, 0, 20).get(0).getId());
        Assertions.assertEquals(3, service.getByState(1L, State.ALL, 0, 20).size());
    }

    @Test
    void checkInputStateAndGetForBookingUserByState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Assertions.assertThrows(ArgumentException.class,
                () -> service.checkInputStateAndGetForBookingUserByState("SOMETHING", 1L, 0, 20));
    }

}
