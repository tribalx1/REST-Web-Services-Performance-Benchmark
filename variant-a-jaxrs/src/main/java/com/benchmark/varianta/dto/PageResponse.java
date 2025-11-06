package com.benchmark.varianta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean last = page >= totalPages - 1;
        boolean first = page == 0;
        
        return new PageResponse<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            last,
            first
        );
    }
}
