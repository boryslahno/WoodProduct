package com.wood_product.repos;

import com.wood_product.domain.Items;
import com.wood_product.domain.ShoppingCart;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart,Long> {
    Iterable<ShoppingCart> findByUser(Users user);

    @Query("SELECT i.user.company.name as companyName, SUM(s.total) as total, SUM(s.total)/2 as toPay FROM items i " +
            "INNER JOIN i.shoppingCart s ON s.item=i " +
            "GROUP BY i.user.company.name")
    Iterable<Map<String,Object>>findByUser();

    @Query("SELECT s FROM items i " +
            "INNER JOIN i.shoppingCart s ON s.item=i WHERE i.user.company.name=:companyName")
    Iterable<ShoppingCart> findByCompany(String companyName);
}
