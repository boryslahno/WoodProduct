package com.wood_product.service;

import com.wood_product.domain.Company;
import com.wood_product.domain.PersonalInformation;
import com.wood_product.domain.Users;
import com.wood_product.repos.CompanyRepository;
import com.wood_product.repos.PersonalInformationRepository;
import com.wood_product.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PersonalInformationRepository personalInformationRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    public String GetUserName(){
        String username;
        Users user;
        PersonalInformation personalInformation;
        Company company;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            user =userRepository.findByUsername(((UserDetails)principal).getUsername());
            if(user.getRoles().iterator().next().toString() =="Покупець"){
                personalInformation=personalInformationRepository.findByUserId(user.getId());
                username=personalInformation.getName()+" "+personalInformation.getSurname();
            }else if(user.getRoles().iterator().next().toString() =="Продавець")
            {
                company=companyRepository.findByUserId(user.getId());
                username=company.getName();
            }else{
                username="Адміністратор";
            }
        } else {
            //username = principal.toString();
            username=null;
        }
        return username;
    }

    public String GetUserRole(){
        String userrole;
        Users user;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            user =userRepository.findByUsername(((UserDetails)principal).getUsername());
            userrole=user.getRoles().iterator().next().toString();
        } else {
            //username = principal.toString();
            userrole=null;
        }
        return userrole;
    }

    public Iterable<Users> loadAllUsers(){
        Iterable<Users> users=userRepository.findAll();
        return users;
    }
}
