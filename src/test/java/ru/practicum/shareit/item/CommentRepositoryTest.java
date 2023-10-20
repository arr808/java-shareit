package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private User owner;
    private User author;
    private Comment comment;
    private final LocalDateTime timestamp = LocalDateTime.now();

    @BeforeEach
    public void createEntity() {
        owner = User.builder()
                .name("owner")
                .email("owner@email.ru")
                .build();

        author = User.builder()
                .name("author")
                .email("author@email.ru")
                .build();

        item = Item.builder()
                .name("name")
                .description("desc")
                .owner(owner)
                .available(true)
                .build();

        userRepository.save(owner);
        userRepository.save(author);
        item = itemRepository.save(item);

        comment = Comment.builder()
                .text("text")
                .item(item)
                .author(author)
                .created(timestamp)
                .build();

        comment = commentRepository.save(comment);
    }

    @Test
    public void shouldReturnCommentsByItemId() {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreated(item.getId());

        Assertions.assertEquals(List.of(comment), comments);
    }

    @Test
    public void shouldDeleteAllByItemId() {
        commentRepository.deleteAllByItemId(item.getId());

        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreated(item.getId());

        Assertions.assertEquals(0, comments.size());
    }

    @Test
    public void shouldNotAddCommentNoText() {
        Comment comment2 = Comment.builder()
                .item(item)
                .author(author)
                .created(timestamp)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment2));
    }

    @Test
    public void shouldNotAddCommentNoItem() {
        Comment comment2 = Comment.builder()
                .text("text")
                .author(author)
                .created(timestamp)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment2));
    }

    @Test
    public void shouldNotAddCommentNoAuthor() {
        Comment comment2 = Comment.builder()
                .text("text")
                .item(item)
                .created(timestamp)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment2));
    }

    @Test
    public void shouldNotAddCommentNoCreated() {
        Comment comment2 = Comment.builder()
                .text("text")
                .item(item)
                .author(author)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> commentRepository.save(comment2));
    }
}
