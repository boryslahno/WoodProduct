package com.wood_product.service;

import com.wood_product.domain.Categories;
import com.wood_product.repos.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Iterable<Categories> loadAllCategories(){
        Iterable<Categories> categories=categoryRepository.findAll();
        return categories;
    }
}
