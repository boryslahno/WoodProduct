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

import java.util.Optional;

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

    public String GetUserName() {
        String username;
        Users user;
        PersonalInformation personalInformation;
        Company company;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            user = userRepository.findByUsername(((UserDetails) principal).getUsername());
            if (user.getRoles().iterator().next().toString() == "Покупець") {
                personalInformation = personalInformationRepository.findByUserId(user.getId());
                username = personalInformation.getName() + " " + personalInformation.getSurname();
            } else if (user.getRoles().iterator().next().toString() == "Продавець") {
                company = companyRepository.findByUserId(user.getId());
                username = company.getName();
            } else {
                username = "Адміністратор";
            }
        } else {
            //username = principal.toString();
            username = null;
        }
        return username;
    }

    public Users GetUserSecurityInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return userRepository.findByUsername(((UserDetails) principal).getUsername());
        } else {
            return null;
        }
    }

    public String GetUserRole() {
        String userrole;
        Users user;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            user = userRepository.findByUsername(((UserDetails) principal).getUsername());
            userrole = user.getRoles().iterator().next().toString();
        } else {
            //username = principal.toString();
            userrole = null;
        }
        return userrole;
    }

    public String GetUserRole(Long id) {
        Optional<Users> users = userRepository.findById(id);
        Users user = users.get();
        String role = user.getRoles().iterator().next().toString();
        return role;
    }

    public Iterable<Users> loadAllUsers() {
        Iterable<Users> users = userRepository.findAll();
        return users;
    }

    public PersonalInformation loadPersonalInfo(Long id) {
        return personalInformationRepository.findByUserId(id);
    }

    public PersonalInformation loadPersonalInfo(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Users user=userRepository.findByUsername(((UserDetails) principal).getUsername());
            return personalInformationRepository.findByUserId(user.getId());
        } else {
            return null;
        }
    }

    public Company loadCompanyInfo(Long id) {
        Company company = companyRepository.findByUserId(id);
        return company;
    }

    public Company loadCompanyInfo(){
        Company company;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            Users user=userRepository.findByUsername(((UserDetails) principal).getUsername());
            return companyRepository.findByUserId(user.getId());
        } else {
            return null;
        }
    }

    public Users currentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return userRepository.findByUsername(((UserDetails) principal).getUsername());
        } else {
            return null;
        }
    }
}
