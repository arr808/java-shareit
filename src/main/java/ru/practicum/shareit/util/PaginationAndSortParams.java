package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.ValidationException;

@UtilityClass
public class PaginationAndSortParams {

    public static Pageable getPageableDesc(int from, int size, String sortBy) {
        validation(from, size);
        return PageRequest.of(from / size, size, Sort.by(sortBy).descending());
    }

    public static Pageable getPageable(int from, int size) {
        validation(from, size);
        return PageRequest.of(from / size, size);
    }

    private void validation(int from, int size) {
        if (from < 0 || size <= 0) throw new ValidationException("pagination params");
    }
}
