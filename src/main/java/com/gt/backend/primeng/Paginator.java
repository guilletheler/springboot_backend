package com.gt.backend.primeng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Paginator implements Serializable {

    public static final Long serialVersionUID = 1L;

    Integer first;
    Integer rows;
    String sortField;
    Integer sortOrder;

    SortMeta[] multiSortMeta;

    Map<String, FilterMetadata> filters;

    String globalFilter;

    public Map<String, String> toFiltersMap() {
        Map<String, String> ret = new HashMap<>();

        if (filters != null) {
            for (Map.Entry<String, FilterMetadata> entry : filters.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().getValue());
            }
        }

        return ret;
    }

    public Pageable toPageable() {
        if(first > 0 && rows % first != 0) {
            throw new IllegalArgumentException("el primer elemento debe ser multiplo de la cantidad de elementos por pagina");
        }
        Sort sorts = null;
        
        if(multiSortMeta == null || multiSortMeta.length == 0) {
            if(sortField != null && !sortField.isEmpty()) {
                if(Optional.ofNullable(sortOrder).orElse(0) >= 0) {
                    sorts = Sort.by(sortField);
                } else {
                    sorts = Sort.by(sortField).descending();
                }
            }
        } else {
            Sort tmpSort = null;
            for(SortMeta sm : multiSortMeta) {
                if(sm.order >= 0) {
                    sorts = Sort.by(sm.field);
                } else {
                    sorts = Sort.by(sm.field).descending();
                }

                if(sorts == null) {
                    sorts = tmpSort;
                } else {
                    sorts = sorts.and(tmpSort);
                }
            }
        }

        int firstPage = 0;

        if(first > 0) {
            firstPage = first / rows;
        }

        if(sorts == null) {
            return PageRequest.of(firstPage, rows);
        }

        return PageRequest.of(firstPage, rows, sorts);
    }
}
