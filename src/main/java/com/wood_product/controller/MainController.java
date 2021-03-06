package com.wood_product.controller;

import com.wood_product.domain.ShoppingCart;
import com.wood_product.repos.CategoryRepository;
import com.wood_product.repos.ShoppingCartRepository;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    UserService userService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @GetMapping("/")
    public String main(Model model){
        model.addAttribute("nameUser",userService.GetUserName());
        boolean admin=false,seller=false,shoper=false,unknown=false;
        if(userService.GetUserRole()=="Адміністратор"){
            admin=true;
        }else if(userService.GetUserRole()=="Покупець"){
            shoper=true;
        }else if(userService.GetUserRole()=="Продавець"){
            seller=true;
        }else
        {
           unknown=true;
        }
        model.addAttribute("isAdmin",admin);
        model.addAttribute("isSeller",seller);
        model.addAttribute("isShoper",shoper);
        model.addAttribute("isUnknown",unknown);
        model.addAttribute("categories",categoryRepository.findAll());
        Iterable<ShoppingCart> itemInCart=shoppingCartRepository.findByUser(userService.currentUser());
        model.addAttribute("itemsInCart",itemInCart);
        if(!itemInCart.iterator().hasNext()){
            model.addAttribute("numberProduct",false);
        }else {
            model.addAttribute("numberProduct","Є товари");
        }
        return "main";
    }
}
