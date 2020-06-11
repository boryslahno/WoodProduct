package com.wood_product.repos;

import com.wood_product.domain.FilterOptions;
import com.wood_product.domain.Filters;
import com.wood_product.domain.Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface FilterOptionsRepository extends JpaRepository<FilterOptions,Long> {
    @Query("SELECT DISTINCT value FROM filter_options WHERE filter=:filter AND filter.category.name=:name")
    List<String> findByFilterAndCategory(Filters filter,String name);

    @Query("SELECT id FROM filter_options WHERE  value IN(:options) GROUP BY item.id HAVING (COUNT(*)=:count)")
    Iterable<Long> findByFilter(String options,Long count);
}
