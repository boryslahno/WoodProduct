package com.wood_product.repos;

import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Long> {
   Users findByUsername(String username);
}
