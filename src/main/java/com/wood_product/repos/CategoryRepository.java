package com.wood_product.repos;

import com.wood_product.domain.Categories;
import com.wood_product.domain.Filters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Categories,Long> {
    Categories findByName(String name);
    @Query("FROM categories c INNER JOIN c.filter f WHERE f.filtername=:filtername AND c.name=:categoryname")
    Categories findByFilternameAndName(String filtername,String categoryname);

}
