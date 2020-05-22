package com.wood_product.controller;

import com.wood_product.domain.Role;
import com.wood_product.domain.Users;
import com.wood_product.repos.UserRepository;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;

@Controller
public class AdministratorController {
    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;

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
}
