package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private User user;
    private User otherUser;

    @BeforeEach
    void setup() {
        user = userService.create(makeUser("User1", "user1@mail.com"));
        otherUser = userService.create(makeUser("User2", "user2@mail.com"));
    }

    @Test
    void create_shouldSaveRequest() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a laptop");

        ItemRequestDto saved = requestService.create(user.getId(), dto);

        assertNotNull(saved.getId());
        assertEquals("Need a laptop", saved.getDescription());
        assertNotNull(saved.getCreated());
    }

    @Test
    void create_shouldThrow_whenUserNotFound() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a laptop");

        assertThrows(NotFoundException.class,
                () -> requestService.create(999L, dto));
    }

    @Test
    void getUserRequests_shouldReturnList() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need laptop");

        requestService.create(user.getId(), dto);

        List<ItemRequestDto> list =
                requestService.getUserRequests(user.getId());

        assertEquals(1, list.size());
    }

    @Test
    void getRequest_shouldThrow_whenNotFound() {
        assertThrows(NotFoundException.class,
                () -> requestService.getRequest(user.getId(), 999L));
    }

    @Test
    void getUserRequests_shouldReturnOnlyOwnRequests() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a laptop");

        requestService.create(user.getId(), dto);
        requestService.create(otherUser.getId(), dto);

        List<ItemRequestDto> result =
                requestService.getUserRequests(user.getId());

        assertEquals(1, result.size());
    }

    @Test
    void getOtherUsersRequests_shouldReturnOthersRequests() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a laptop");

        requestService.create(user.getId(), dto);
        requestService.create(otherUser.getId(), dto);

        List<ItemRequestDto> result =
                requestService.getOtherUsersRequests(user.getId());

        assertEquals(1, result.size());
    }

    @Test
    void getRequest_shouldReturnRequestWithItems() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a laptop");

        ItemRequestDto request =
                requestService.create(user.getId(), dto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Laptop");
        itemDto.setDescription("Black");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());

        itemService.create(otherUser.getId(), itemDto);

        ItemRequestDto result =
                requestService.getRequest(user.getId(), request.getId());

        assertEquals(1, result.getItems().size());
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
