package com.wood_product.service;

import com.wood_product.domain.Filters;
import com.wood_product.repos.FilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilterService {
    @Autowired
    private FilterRepository filterRepository;

    public Iterable<Filters> loadAllFilters(){
        return filterRepository.findAll();
    }
}
