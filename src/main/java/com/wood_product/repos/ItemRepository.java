package com.wood_product.repos;

import com.wood_product.domain.Categories;
import com.wood_product.domain.Filters;
import com.wood_product.domain.Items;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Items,Long> {
    Iterable<Items> findByUser(Users user);
    Iterable<Items> findByCategory(Categories category);
    @Query("SELECT i FROM items i INNER JOIN i.filterOptions f ON f.item=i WHERE f.value=:option AND i.category.name=:name AND i.price BETWEEN 0 AND :price")
    Iterable<Items> findByFilter(String option,String name,Integer price);
}
