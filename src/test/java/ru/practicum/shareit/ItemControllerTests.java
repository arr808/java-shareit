package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTests {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private static final String ITEMS_QUERY = "/items";
    private static final String USERS_QUERY = "/users";

    private final UserDto userDto = UserDto.builder()
            .name("User")
            .email("user@test.ru")
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .name("item")
            .description("description")
            .available(true)
            .build();

    private final Item item = Item.builder()
            .id(1)
            .name("item")
            .description("description")
            .ownerId(1)
            .available(true)
            .build();

    @BeforeEach
    public void create() throws Exception {
        mockMvc.perform(post(USERS_QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @AfterEach
    public void clear() throws Exception {
        mockMvc.perform(delete(USERS_QUERY));
    }

    @Test
    public void shouldAddAndReturnItem() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.getDto(item));

        mockMvc.perform(get(ITEMS_QUERY)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        mockMvc.perform(get(ITEMS_QUERY + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));
    }

    @Test
    public void shouldNotAddItemWithUnknownUser() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotAddItemWithOutUser() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotReturnItemWithUnknownUser() throws Exception {
        mockMvc.perform(get(ITEMS_QUERY + "/1")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is(404));

        mockMvc.perform(get(ITEMS_QUERY)
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotReturnItemWithOutUser() throws Exception {
        mockMvc.perform(get(ITEMS_QUERY + "/1"))
                .andExpect(status().is(400));

        mockMvc.perform(get(ITEMS_QUERY))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddItemWithOutName() throws Exception {
        itemDto.setName(null);
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddItemWithOutDescription() throws Exception {
        itemDto.setDescription(null);
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotAddItemWithOutAvailable() throws Exception {
        itemDto.setAvailable(null);
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldUpdateItemName() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        ItemDto updateDto = ItemDto.builder()
                .name("update")
                .build();
        item.setName("update");

        mockMvc.perform(patch(ITEMS_QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));
    }

    @Test
    public void shouldUpdateItemDescription() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        ItemDto updateDto = ItemDto.builder()
                .description("update")
                .build();
        item.setDescription("update");

        mockMvc.perform(patch(ITEMS_QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));
    }

    @Test
    public void shouldUpdateItemAvailable() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        ItemDto updateDto = ItemDto.builder()
                .available(false)
                .build();
        item.setAvailable(false);

        mockMvc.perform(patch(ITEMS_QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));
    }

    @Test
    public void shouldUpdateAllItemParams() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        ItemDto updateDto = ItemDto.builder()
                .name("update")
                .description("update")
                .available(false)
                .build();
        item.setName("update");
        item.setDescription("update");
        item.setAvailable(false);

        mockMvc.perform(patch(ITEMS_QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));
    }

    @Test
    public void shouldNotUpdateItemFromUnknownUser() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        ItemDto updateDto = ItemDto.builder()
                .name("update")
                .description("update")
                .available(false)
                .build();
        item.setName("update");
        item.setDescription("update");
        item.setAvailable(false);

        mockMvc.perform(patch(ITEMS_QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotUpdateItemFromAnotherUser() throws Exception {
        userDto.setEmail("newUser@test.ru");
        mockMvc.perform(post(USERS_QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        ItemDto updateDto = ItemDto.builder()
                .name("update")
                .description("update")
                .available(false)
                .build();
        item.setName("update");
        item.setDescription("update");
        item.setAvailable(false);

        mockMvc.perform(patch(ITEMS_QUERY + "/1")
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldSearchItemByNameUsingText() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.getDto(item));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=item")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    public void shouldSearchItemByNameUsingPartOfText() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.getDto(item));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=te")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    public void shouldSearchItemByDescriptionUsingText() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.getDto(item));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=description")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    public void shouldSearchItemByDescriptionUsingPartOfText() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.getDto(item));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=crip")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    public void shouldSearchItemByEmptyTextAndReturnEmptyList() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<>())));
    }

    @Test
    public void shouldSearchItemByNameUsingTextFromAnotherUser() throws Exception {
        userDto.setEmail("newUser@test.ru");
        mockMvc.perform(post(USERS_QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        List<ItemDto> items = new ArrayList<>();
        items.add(ItemMapper.getDto(item));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=item")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    public void shouldNotSearchItemFromUnknownUser() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        mockMvc.perform(get(ITEMS_QUERY + "/search?text=item")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldDeleteItemById() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        mockMvc.perform(delete(ITEMS_QUERY + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldNotDeleteUnknownItemById() throws Exception {
        mockMvc.perform(delete(ITEMS_QUERY + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is(404));
    }

    @Test
    public void shouldNotDeleteItemByIdFromAnotherUser() throws Exception {
        userDto.setEmail("newUser@test.ru");
        mockMvc.perform(post(USERS_QUERY)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        mockMvc.perform(delete(ITEMS_QUERY + "/1")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is(400));
    }

    @Test
    public void shouldNotDeleteItemByIdFromUnknownUser() throws Exception {
        mockMvc.perform(post(ITEMS_QUERY)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ItemMapper.getDto(item))));

        mockMvc.perform(delete(ITEMS_QUERY + "/1")
                        .header("X-Sharer-User-Id", 999))
                .andExpect(status().is(404));
    }
}
