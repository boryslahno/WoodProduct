package com.wood_product.repos;

import com.wood_product.domain.Comments;
import com.wood_product.domain.Items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentsRepository extends JpaRepository<Comments,Long> {
    Iterable<Comments> findByItem(Items item);
}
