package com.wood_product.repos;

import com.wood_product.domain.Filters;
import com.wood_product.domain.Items;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Items,Long> {
    Iterable<Items> findByUser(Users user);
}
