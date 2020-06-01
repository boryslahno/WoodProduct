package com.wood_product.controller;

import com.wood_product.domain.Items;
import com.wood_product.repos.CategoryRepository;
import com.wood_product.repos.ItemRepository;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Controller
public class ShopperController {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Value("${upload.path}")
    private String uploadPath;
    @GetMapping("/productList")
    public String productList(Model model, @RequestParam String categoryName){
        Iterable<Items> items=itemRepository.findByCategory(categoryRepository.findByName(categoryName));
        for (Items item:items) {
            Image image=new ImageIcon(uploadPath+item.getFileName()).getImage();
            if(image.getHeight(null)>300){
                item.setSize(true);
            }else{
                item.setSize(false);
            }
        }
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items",items);
        model.addAttribute("categories",categoryRepository.findAll());
        model.addAttribute("NameCategory",categoryName);
        return "productList";
    }
}
