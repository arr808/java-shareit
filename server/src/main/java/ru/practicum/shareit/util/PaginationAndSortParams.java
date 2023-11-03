package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PaginationAndSortParams {

    public static Pageable getPageable(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    public static Pageable getPageableAsc(int from, int size, String sortBy) {
        return PageRequest.of(from / size, size, Sort.by(sortBy));
    }

    public static Pageable getPageableDesc(int from, int size, String sortBy) {
        return PageRequest.of(from / size, size, Sort.by(sortBy).descending());
    }
}
