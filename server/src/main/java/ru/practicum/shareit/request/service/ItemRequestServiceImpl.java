package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        log.info("Создание запроса. userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь {} не найден", userId);
                    return new NotFoundException("Пользователь не найден");
                });

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        log.info("Запрос {} успешно создан", saved.getId());
        return mapToDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        log.info("Получение запросов пользователя {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests =
                requestRepository.findByRequestorIdOrderByCreatedDesc(userId);

        return enrichRequests(requests);
    }

    @Override
    public List<ItemRequestDto> getOtherUsersRequests(Long userId) {
        log.info("Получение запросов других пользователей. userId={}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests =
                requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);

        return enrichRequests(requests);
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        log.info("Получение запроса {} пользователем {}", requestId, userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        return enrichRequests(List.of(request)).get(0);
    }

    private List<ItemRequestDto> enrichRequests(List<ItemRequest> requests) {

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        Map<Long, List<Item>> itemsMap = itemRepository
                .findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));

        return requests.stream()
                .map(r -> mapToDto(
                        r,
                        itemsMap.getOrDefault(r.getId(), List.of())
                ))
                .toList();
    }

    private ItemRequestDto mapToDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        dto.setItems(
                items.stream()
                        .map(i -> new ItemResponseDto(
                                i.getId(),
                                i.getName(),
                                i.getOwner().getId()
                        ))
                        .toList()
        );
        return dto;
    }
}

