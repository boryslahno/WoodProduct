package com.wood_product.repos;

import com.wood_product.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company,Long> {
    Company findByUserId(Long userId);
}
