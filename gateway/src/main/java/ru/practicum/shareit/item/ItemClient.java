package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAll(long userId, int from, int size) {
        return get("?&from={from}&size={size}", userId, getParameters(from, size));
    }

    public ResponseEntity<Object> searchByText(String text, int from, int size) {
        return get("/search?&text={text}&from={from}&size={size}", null, getParameters(text, from, size));
    }

    public ResponseEntity<Object> add(ItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(long itemId, ItemDto itemDto, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> deleteById(long itemId, long userId) {
        return delete("/" + itemId, userId);
    }

    private Map<String, Object> getParameters(int from, int size) {
        return Map.of("from", from, "size", size);
    }

    private Map<String, Object> getParameters(String text, int from, int size) {
        return Map.of("text", text, "from", from, "size", size);
    }
}
