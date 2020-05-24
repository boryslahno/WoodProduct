package com.wood_product.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wood_product.domain.*;
import com.wood_product.repos.CategoryRepository;
import com.wood_product.repos.CompanyRepository;
import com.wood_product.repos.PersonalInformationRepository;
import com.wood_product.repos.UserRepository;
import com.wood_product.service.CategoryService;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import java.util.Collections;
import java.util.Optional;

@Controller
public class AdministratorController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PersonalInformationRepository personalInformationRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    //User management
    private Long IDUser;
    @GetMapping("/adminUserList")
    public String adminUserList(Model model){
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        return "adminUserList";
    }
    @PostMapping("/adminUserList")
    public String addAdmin(Users user,Model model){
        Users userFromDB=userRepository.findByUsername(user.getUsername());
        if(userFromDB!=null){
            model.addAttribute("exists","Користувач з такою електронною поштою вже існує");
        }else{
            user.setRoles(Collections.singleton(Role.Адміністратор));
            userRepository.save(user);
            model.addAttribute("addAdmin","Адміністратор успішно доданий");
        }
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        return "adminUserList";
    }
    @GetMapping("/adminUserList/users")
    public String reviewUser(@RequestParam Long userID, Model model) {
        IDUser=userID;
        String role=userService.GetUserRole(userID);
        if(role=="Продавець"){
            model.addAttribute("sellerPerInfo",userService.loadCompanyInfo(userID));
            model.addAttribute("shopperPerInf",null);
            model.addAttribute("adminPerInfo",null);
        }else if(role=="Покупець"){
            model.addAttribute("sellerPerInfo",null);
            model.addAttribute("shopperPerInf",userService.loadPersonalInfo(userID));
            model.addAttribute("adminPerInfo",null);
        }else{
            model.addAttribute("sellerPerInfo",null);
            model.addAttribute("shopperPerInf",null);
            model.addAttribute("adminPerInfo","Особиста інформація відсутня");
        }
        model.addAttribute("id",userID);
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        return "adminUserList";
        }
    @PostMapping("/adminUserList/seller/edit")
    public String editUser(@RequestParam String name, @RequestParam String address, @RequestParam String phoneNumber,Model model){
        Optional<Users> users=userRepository.findById(IDUser);
        Users user=users.get();
        Company company=user.getCompany();
        company.setName(name);
        company.setAddress(address);
        company.setPhoneNumber(phoneNumber);
        companyRepository.save(company);
        model.addAttribute("updateSeller","Особиста інформація продавця успішно оновлена");
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        return "adminUserList";
        }
    @PostMapping("/adminUserList/shopper/edit")
    public String editUser(@RequestParam String name, @RequestParam String surname, @RequestParam String address, @RequestParam String phoneNumber,Model model){
        Optional<Users> users=userRepository.findById(IDUser);
        Users user=users.get();
        PersonalInformation personalInformation=user.getPersonalInformation();
        personalInformation.setName(name);
        personalInformation.setSurname(surname);
        personalInformation.setAddress(address);
        personalInformation.setPhoneNumber(phoneNumber);
        personalInformationRepository.save(personalInformation);
        model.addAttribute("updateShopper","Особиста інформація покупця успішно оновлена");
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        return "adminUserList";
    }
    @GetMapping("/adminUserList/delete/user")
    public String deleteUser(@RequestParam Long userID,Model model){
        IDUser=userID;
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        model.addAttribute("deleteUser","Ви дійсно бажаєте видадалити цього користувача?");
        return "adminUserList";
    }
    @PostMapping("/adminUserList/delete/user")
    public String deleteUser(Model model){
        userRepository.deleteById(IDUser);
        model.addAttribute("deleteSuccess","Користувач успішно видалений");
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("users",userService.loadAllUsers());
        return "adminUserList";
    }

    //Category product management
    private Long IDCategory;
    @GetMapping("/adminCategoryList")
    public String adminCategoryList(Model model){
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("categories",categoryService.loadAllCategories());
        return "/adminCategoryList";
    }
    @PostMapping("/adminCategoryList")
    public String addCategory(Categories cetegory,Model model){
        Categories categories=categoryRepository.findByName(cetegory.getName());
        if(categories!=null){
            model.addAttribute("exists","Категорія з такою назвою вже існує");
        }else{
            categoryRepository.save(cetegory);
            model.addAttribute("addCategory","Категорія успішно додана");
        }
        model.addAttribute("nameUser",userService.GetUserName());
        model.addAttribute("categories",categoryService.loadAllCategories());
        return "/adminCategoryList";
    }
    @GetMapping("/adminCategoryList/edit/category")
    public String editCategory(@RequestParam String categoryID, Model model){

        return "adminCategoryList";
    }
}

