package com.wood_product.repos;

import com.wood_product.domain.ShoppingCart;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {
    Iterable<ShoppingCart> findByUser(Users user);
}
