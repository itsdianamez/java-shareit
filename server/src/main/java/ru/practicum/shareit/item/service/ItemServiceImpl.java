package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public Item create(Long ownerId, ItemDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            log.warn("Нет названия вещи");
            throw new BadRequestException("Отсутствует название");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            log.warn("Нет описания вещи");
            throw new BadRequestException("Отсутствует описание");
        }

        ItemRequest request = null;

        if (dto.getRequestId() != null) {
            request = requestRepository.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);

        Item savedItem = itemRepository.save(item);
        log.info("Вещь {} создана успешно", savedItem.getId());
        return savedItem;
    }

    @Override
    public Item update(Long ownerId, Long itemId, ItemDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            log.warn("Пользователь {} не является владельцем вещи {}", ownerId, itemId);
            throw new ForbiddenException("Только владелец может обновлять вещь");
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }

        Item updatedItem = itemRepository.save(item);
        log.info("Вещь {} обновлена успешно", updatedItem.getId());
        return updatedItem;
    }

    @Override
    public ItemWithBookingsDto get(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Вещь {} не найдена", itemId);
                    return new NotFoundException("Вещь не найдена");
                });

        ItemWithBookingsDto dto = new ItemWithBookingsDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(List.of());

        dto.setComments(
                commentRepository.findByItemId(itemId)
                        .stream()
                        .map(c -> {
                            CommentDto cd = new CommentDto();
                            cd.setId(c.getId());
                            cd.setText(c.getText());
                            cd.setAuthorName(c.getAuthor().getName());
                            cd.setCreated(c.getCreated());
                            return cd;
                        }).toList()
        );

        if (item.getOwner().getId().equals(userId)) {

            LocalDateTime now = LocalDateTime.now();

            bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                            itemId, now, BookingStatus.APPROVED
                    )
                    .ifPresent(b ->
                            dto.setLastBooking(
                                    new BookingShortDto(
                                            b.getId(),
                                            b.getBooker().getId()
                                    )
                            )
                    );

            bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            itemId, now, BookingStatus.APPROVED
                    )
                    .ifPresent(b ->
                            dto.setNextBooking(
                                    new BookingShortDto(
                                            b.getId(),
                                            b.getBooker().getId()
                                    )
                            )
                    );
        }

        log.info("Вещь {} успешно получена пользователем {}", itemId, userId);
        return dto;
    }

    @Override
    public List<ItemWithBookingsDto> getOwnerItems(Long ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, List<Comment>> commentsMap = commentRepository.findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastBookings = bookingRepository
                .findLastBookings(itemIds, now, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (a, b) -> a
                ));

        Map<Long, Booking> nextBookings = bookingRepository
                .findNextBookings(itemIds, now, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (a, b) -> a
                ));

        List<ItemWithBookingsDto> result = new ArrayList<>();

        for (Item item : items) {
            ItemWithBookingsDto dto = new ItemWithBookingsDto();
            dto.setId(item.getId());
            dto.setName(item.getName());
            dto.setDescription(item.getDescription());
            dto.setAvailable(item.getAvailable());

            List<CommentDto> commentDtos = commentsMap
                    .getOrDefault(item.getId(), List.of())
                    .stream()
                    .map(c -> {
                        CommentDto cd = new CommentDto();
                        cd.setId(c.getId());
                        cd.setText(c.getText());
                        cd.setAuthorName(c.getAuthor().getName());
                        cd.setCreated(c.getCreated());
                        return cd;
                    })
                    .toList();

            dto.setComments(commentDtos);

            Booking last = lastBookings.get(item.getId());
            if (last != null) {
                dto.setLastBooking(new BookingShortDto(
                        last.getId(),
                        last.getBooker().getId()
                ));
            }

            Booking next = nextBookings.get(item.getId());
            if (next != null) {
                dto.setNextBooking(new BookingShortDto(
                        next.getId(),
                        next.getBooker().getId()
                ));
            }

            result.add(dto);
        }

        log.info("Найдено {} вещей владельца {}", result.size(), ownerId);
        return result;
    }

    public List<Item> search(String text) {
        log.info("Поиск вещей по тексту: '{}'", text);
        if (text == null || text.isBlank()) return List.of();
        List<Item> items = itemRepository.search(text);
        log.info("Найдено {} вещей по запросу '{}'", items.size(), text);
        return items;
    }

    @Override
    public Comment addComment(Long userId, Long itemId, String text) {
        boolean hasBooking = bookingRepository
                .existsByItemIdAndBookerIdAndEndBefore(
                        itemId,
                        userId,
                        LocalDateTime.now()
                );
        if (!hasBooking) {
            log.warn("Пользователь {} не арендовал вещь {}", userId, itemId);
            throw new BadRequestException("Пользователь не арендовал вещь");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий {} к вещи {} создан успешно", savedComment.getId(), itemId);
        return savedComment;
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}