package com.DucPhuc.Plants_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PagingResponse<T> {

    List<T> items;
    int currentPage;
    int totalPages;
    long totalItems;

    public static <T> PagingResponse<T> of(List<T> items, int currentPage, int totalPages, long totalItems) {
        return PagingResponse.<T>builder()
                .items(items)
                .currentPage(currentPage)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build();
    }
}
