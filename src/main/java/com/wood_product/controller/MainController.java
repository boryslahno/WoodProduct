package com.wood_product.controller;


import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String greeting(Model model) {
        model.addAttribute("nameUser",userService.GetUserName());
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Model model){
        model.addAttribute("nameUser",userService.GetUserName());
        return "main";
    }


}
