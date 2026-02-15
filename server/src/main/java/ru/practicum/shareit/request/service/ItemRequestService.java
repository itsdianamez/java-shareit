package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestCreateDto dto);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getOtherUsersRequests(Long userId);

    ItemRequestDto getRequest(Long userId, Long requestId);
}

