package com.wood_product.controller;


import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wood_product.domain.Company;
import com.wood_product.domain.PersonalInformation;
import com.wood_product.domain.Role;
import com.wood_product.domain.Users;
import com.wood_product.repos.CompanyRepository;
import com.wood_product.repos.PersonalInformationRepository;
import com.wood_product.repos.UserRepository;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonalInformationRepository personalInformationRepository;
    @Autowired
    private CompanyRepository companyRepository;

    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @GetMapping("/registration/shopper")
    public String shopper(){
        return "shopperRegistration";
    }

    @Transactional
    @PostMapping("/registration/shopper")
    public String addShopper(Users user, PersonalInformation personalInformation, Model model) {
        Users userFromDB=userRepository.findByUsername(user.getUsername());
        if(userFromDB!=null){
            model.addAttribute("message","User exists!");
            return "shopperRegistration";
        }
        Date currentDate=new Date();
        user.setRegisterDate(currentDate);
        userRepository.save(user);
        personalInformation.setUser(user);
        personalInformationRepository.save(personalInformation);
        return "redirect:/login";
    }
    @GetMapping("/registration/seller")
    public String seller(){
        return "sellerRegistration";
    }

    @Transactional
    @PostMapping("/registration/seller")
    public String addSeller(Users user, Company company, Model model){
        Users userFromDB=userRepository.findByUsername(user.getUsername());
        if(userFromDB!=null){
            model.addAttribute("message","User exists!");
            return "sellerRegistration";
        }
        Date currentDate=new Date();
        user.setRegisterDate(currentDate);
        userRepository.save(user);
        userRepository.save(user);
        company.setUser(user);
        companyRepository.save(company);
        return "redirect:/login";
    }
    @GetMapping("/registration/admin")
    public String admin(){
        return "adminRegistration";
    }

    @Transactional
    @PostMapping("/registration/admin")
    public String addSeller(Users user, Model model){
        Users userFromDB=userRepository.findByUsername(user.getUsername());
        if(userFromDB!=null){
            model.addAttribute("message","User exists!");
            return "adminRegistration";
        }
        Date currentDate=new Date();
        user.setRegisterDate(currentDate);
        userRepository.save(user);
        userRepository.save(user);;
        return "redirect:/login";
    }
}
