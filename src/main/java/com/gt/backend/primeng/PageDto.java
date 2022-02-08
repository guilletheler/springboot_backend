package com.gt.backend.primeng;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto<T> implements Serializable {
    public static long serialVersionUID = 1L;

    private Integer first;
    private Integer rows;
    private Long totalElements;

    private List<T> elements;
}
