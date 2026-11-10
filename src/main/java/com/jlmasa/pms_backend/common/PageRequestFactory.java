package com.jlmasa.pms_backend.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageRequestFactory {

    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private PageRequestFactory(){
    }

    public static Pageable build(Integer page, Integer size, String sortField, String sortDirection, Set<String> sortWhitelist, String tiebreaker){
        int resolvedPage = (page == null || page <0) ? 0 : page;
        int resolvedSize = (page == null || size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String field = sortField;
        if(field == null || !sortWhitelist.contains(field)){
            if (field != null){
                throw new IllegalArgumentException("sortField must be one of "+ sortWhitelist);
            }
            field = tiebreaker;
        }
        Sort sort = field.equals(tiebreaker)
                ? Sort.by(direction, tiebreaker)
                : Sort.by(direction,field).and(Sort.by(Sort.Direction.ASC, tiebreaker));
        return PageRequest.of(resolvedPage, resolvedSize, sort);
    }
}
