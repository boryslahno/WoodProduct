package com.wood_product.repos;

import com.wood_product.domain.Items;
import com.wood_product.domain.Shopping;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ShoppingRepository extends JpaRepository<Shopping,Long> {
    Iterable<Shopping> findByUser(Users user);

    @Query("SELECT s FROM items i " +
            "INNER JOIN i.shoppings s ON s.item=i WHERE i.user=:user")
    Iterable<Shopping> findByOwner(Users user);

    @Query("SELECT SUM(s.total) FROM shopping s INNER JOIN s.item i ON s.item=i WHERE i.user=:user AND s.shopDate BETWEEN :start AND :end GROUP BY i.user")
    Integer findSumSold(Users user,Date start,Date end);

    @Query("SELECT SUM(s.quantity) FROM shopping s INNER JOIN s.item i ON s.item=i WHERE i.user=:user AND s.shopDate BETWEEN :start AND :end GROUP BY i.user")
    Integer findCountSold(Users user,Date start,Date end);

    @Query("SELECT i.category.name as name, SUM(s.quantity) as quantity FROM shopping s INNER JOIN s.item i ON s.item=i WHERE i.user=:user AND s.shopDate BETWEEN :start AND :end GROUP BY i.category.name")
    Iterable<Map<String,Object>> findCategoryCountSold(Users user,Date start,Date end);

    @Query("SELECT i as item,i.category.name as name,SUM(s.quantity) as quantity FROM shopping s INNER JOIN s.item i ON s.item=i WHERE i.user=:user AND s.shopDate BETWEEN :start AND :end GROUP BY i,i.category.name ORDER BY quantity DESC")
    List<Map<String,Object>> findTopItems(Users user,Date start,Date end);

}

