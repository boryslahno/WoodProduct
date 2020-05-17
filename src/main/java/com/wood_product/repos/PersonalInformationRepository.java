package com.wood_product.repos;

import com.wood_product.domain.PersonalInformation;
import com.wood_product.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalInformationRepository extends JpaRepository<PersonalInformation,Long> {
    PersonalInformation findByUserId(Long userId);
}
