package com.wood_product.repos;

import com.wood_product.domain.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Categories,Long> {
    Categories findByName(String name);
}
