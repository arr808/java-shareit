package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private ItemRequestShortDto itemRequestShortDto;
    private final long requestId = 1;
    private final long userId = 1;
    private static final String QUERY = "/requests";
    private final LocalDateTime timestamp = LocalDateTime.now();

    @BeforeEach
    public void createEntity() {
        itemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description("desc")
                .created(timestamp)
                .build();

        itemRequestShortDto = ItemRequestShortDto.builder()
                .description("desc")
                .creation(timestamp)
                .build();
    }

    @Test
    public void shouldReturnRequestsByUser() throws Exception {
        when(itemRequestService.getAllByUser(userId))
                .thenReturn((List.of(itemRequestDto)));

        mvc.perform(get(QUERY)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    public void shouldReturnRequests() throws Exception {
        when(itemRequestService.getAll(userId, 0, 20))
                .thenReturn((List.of(itemRequestDto)));

        mvc.perform(get(QUERY + "/all?from=0&size=20")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    public void shouldReturnRequestById() throws Exception {
        when(itemRequestService.getById(userId, requestId))
                .thenReturn((itemRequestDto));

        mvc.perform(get(QUERY + "/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    public void shouldAddRequest() throws Exception {
        mvc.perform(post(QUERY)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemRequestShortDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}