package com.gt.backend.primeng;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterMetadata implements Serializable {

    public final static long serialVersionUID = 1L;

    private String value;
    private String matchMode;
    private String operator;
}
