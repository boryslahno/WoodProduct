package com.wood_product.repos;

import com.wood_product.domain.Categories;
import com.wood_product.domain.Filters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filters,Long> {
    /*@Query("FROM filters WHERE filtername=:name GROUP BY filtername")
    Filters findByFilternameGroupBY(@Param("name") String name);*/
    List<Filters> findByFiltername(String name);

    @Query("SELECT f.id FROM categories c INNER JOIN c.filter f WHERE f.filtername=:filtername AND c.name=:categoryname")
    Long findByFilternameAndName(String filtername, String categoryname);
    //Filters findByFiltername(String name);
    Iterable<Filters> findByCategory(Categories category);
}
