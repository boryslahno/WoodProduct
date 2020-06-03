package com.wood_product.repos;

import com.wood_product.domain.Items;
import com.wood_product.domain.Shopping;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingRepository extends JpaRepository<Shopping,Long> {
    Iterable<Shopping> findByUser(Users user);

    @Query("SELECT s FROM items i " +
            "INNER JOIN i.shoppings s ON s.item=i WHERE i.user=:user")
    Iterable<Shopping> findByOwner(Users user);
}
