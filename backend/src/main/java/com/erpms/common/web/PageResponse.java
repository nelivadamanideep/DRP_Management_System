package com.erpms.common.web;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Serializable, framework-agnostic page envelope returned by paginated APIs.
 *
 * <p>We deliberately avoid returning Spring's {@code PageImpl} because its
 * JSON contract is unstable across versions.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
