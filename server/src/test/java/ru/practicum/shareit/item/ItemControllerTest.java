package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    ItemService itemService;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private final long itemId = 1;
    private final long userId = 1;
    private final long commentId = 1;
    private static final String QUERY = "/items";
    private final LocalDateTime timestamp = LocalDateTime.now();

    @BeforeEach
    public void createEntity() {
        itemDto = ItemDto.builder()
                .id(itemId)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(commentId)
                .text("text")
                .authorName("name")
                .created(timestamp)
                .build();
    }

    @Test
    public void shouldReturnItemById() throws Exception {
        when(itemService.getById(itemId, userId))
                .thenReturn((itemDto));

        mvc.perform(get(QUERY + "/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    public void shouldReturnItems() throws Exception {
        when(itemService.getAll(userId, 0, 20))
                .thenReturn((List.of(itemDto)));

        mvc.perform(get(QUERY + "/?from=0&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    public void shouldReturnSearchedItems() throws Exception {
        when(itemService.searchByText("name", 0, 20))
                .thenReturn((List.of(itemDto)));

        mvc.perform(get(QUERY + "/search?text=name&from=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    public void shouldAddItem() throws Exception {
        ItemDto noIdItemDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .build();

        when(itemService.add(noIdItemDto, userId))
                .thenReturn(itemDto);

        mvc.perform(post(QUERY)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(noIdItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    public void shouldAddComment() throws Exception {
        CommentDto noIdCommentDto = CommentDto.builder()
                .text("text")
                .authorName("name")
                .created(timestamp)
                .build();

        when(itemService.addComment(itemId, userId, noIdCommentDto))
                .thenReturn(commentDto);

        mvc.perform(post(QUERY + "/1/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(noIdCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));
    }

    @Test
    public void shouldUpdateItem() throws Exception {
        ItemDto updateDto = ItemDto.builder()
                .id(itemId)
                .name("new name")
                .description("desc")
                .available(true)
                .build();
        itemDto.setName("new name");

        when(itemService.update(updateDto, userId))
                .thenReturn(itemDto);

        mvc.perform(patch(QUERY + "/1")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDto)));
    }

    @Test
    public void shouldDeleteUserItemById() throws Exception {
        mvc.perform(delete(QUERY + "/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}