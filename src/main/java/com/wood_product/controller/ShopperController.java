package com.wood_product.controller;

import com.wood_product.domain.*;
import com.wood_product.repos.*;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonalInformationRepository personalInformationRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    //Product management
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
        Iterable<ShoppingCart> itemInCart=shoppingCartRepository.findByUser(userService.currentUser());
        model.addAttribute("itemsInCart",itemInCart);
        if(!itemInCart.iterator().hasNext()){
            model.addAttribute("numberProduct",false);
        }else {
            model.addAttribute("numberProduct","Є товари");
        }
        return "productList";
    }

    @GetMapping("/viewProduct")
    public String viewProduct(Model model,@RequestParam Long itemID){
        Items item=itemRepository.findById(itemID).get();
        model.addAttribute("item",item);
        model.addAttribute("nameUser", userService.GetUserName());
        Image image=new ImageIcon(uploadPath+item.getFileName()).getImage();
        if(image.getHeight(null)>=image.getWidth(null)){
            model.addAttribute("heightSize",100);
        }else{
            model.addAttribute("widthSize",100);
        }
        model.addAttribute("categories",categoryRepository.findAll());
        Iterable<ShoppingCart> itemInCart=shoppingCartRepository.findByUser(userService.currentUser());
        model.addAttribute("itemsInCart",itemInCart);
        if(!itemInCart.iterator().hasNext()){
            model.addAttribute("numberProduct",false);
        }else {
            model.addAttribute("numberProduct","Є товари");
        }
        return "viewProduct";
    }

    @GetMapping("/shoppingCart")
    public String shoppingCart(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories",categoryRepository.findAll());
        Iterable<ShoppingCart> itemInCart=shoppingCartRepository.findByUser(userService.currentUser());
        model.addAttribute("itemsInCart",itemInCart);
        if(!itemInCart.iterator().hasNext()){
            model.addAttribute("numberProduct",false);
        }else {
            model.addAttribute("numberProduct","Є товари");
        }
        Map<String,Object> orderDetails=new HashMap<String,Object>();

        return "shoppingCart";
    }

    @PostMapping("/deleteProductWithCart")
    public String deleteProductWithCart(@RequestParam Long itemID){
        shoppingCartRepository.deleteById(itemID);
        return "redirect:/shoppingCart";
    }

    @PostMapping("/addToCart")
    public String addToChart(@RequestParam Long itemID,@RequestParam Integer count){
        Items item=itemRepository.findById(itemID).get();
        ShoppingCart shoppingCart=new ShoppingCart();
        shoppingCart.setQuantity(count);
        shoppingCart.setItem(item);
        shoppingCart.setUser(userService.currentUser());
        shoppingCart.setTotal(count*item.getPrice());
        shoppingCartRepository.save(shoppingCart);
        return "redirect:/shoppingCart";
    }
    //Personal information management
    @GetMapping("/shopperPersonalInformation")
    public String personalInformation(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("shopperSecurity", userService.GetUserSecurityInfo());
        model.addAttribute("personalInfo", userService.loadPersonalInfo());
        model.addAttribute("categories",categoryRepository.findAll());
        Iterable<ShoppingCart> itemInCart=shoppingCartRepository.findByUser(userService.currentUser());
        model.addAttribute("itemsInCart",itemInCart);
        if(!itemInCart.iterator().hasNext()){
            model.addAttribute("numberProduct",false);
        }else {
            model.addAttribute("numberProduct","Є товари");
        }
        return "shopperPersonalInformation";
    }

    @GetMapping("/shopperPersonalInformation/changeEmail")
    public String changeEmail(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("shopperChangeEmail", userService.GetUserSecurityInfo());
        return "redirect:/shopperPersonalInformation";
    }

    @PostMapping("/shopperPersonalInformation/changeEmail")
    public String changeEmail(@RequestParam String username, RedirectAttributes redirectAttributes) {
        Users user = userService.GetUserSecurityInfo();
        Users userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null) {
            redirectAttributes.addFlashAttribute("exists", "Користувач з такою електронною поштою вже існує");
            return "redirect:/shopperPersonalInformation";
        } else {
            user.setUsername(username);
            userRepository.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/shopperPersonalInformation/changePassword")
    public String changePassword(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("shopperChangePassword", true);
        return "redirect:/shopperPersonalInformation";
    }

    @PostMapping("/shopperPersonalInformation/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                 @RequestParam String repeatPassword, RedirectAttributes redirectAttributes) {
        Users user = userService.GetUserSecurityInfo();
        if (!oldPassword.equals(user.getPassword())) {
            redirectAttributes.addFlashAttribute("oldPasswordError", "Введено неправильний старий пароль");
            return "redirect:/shopperPersonalInformation";
        } else if (!newPassword.equals(repeatPassword)) {
            redirectAttributes.addFlashAttribute("repeatPasswordError", "Паролі не співпадають");
            return "redirect:/shopperPersonalInformation";
        } else {
            user.setPassword(newPassword);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("changePassword", "Пароль змінено успішно");
            return "redirect:/shopperPersonalInformation";
        }
    }

    @GetMapping("/shopperPersonalInformation/changePersonalInformation")
    public String changeCompanyInformation(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("shopperChangePersonalInfo", userService.loadPersonalInfo());
        return "redirect:/shopperPersonalInformation";
    }

    @PostMapping("/shopperPersonalInformation/changePersonalInformation")
    public String changeCompanyInformation(RedirectAttributes redirectAttributes, @RequestParam String name,
                                           @RequestParam String address, @RequestParam String surname,
                                           @RequestParam String phoneNumber) {
        PersonalInformation personalInformation=userService.loadPersonalInfo();
        personalInformation.setName(name);
        personalInformation.setSurname(surname);
        personalInformation.setAddress(address);
        personalInformation.setPhoneNumber(phoneNumber);
        personalInformationRepository.save(personalInformation);
        redirectAttributes.addFlashAttribute("changePerlInfo", "Особиста інформація успішно оновлена");
        return "redirect:/shopperPersonalInformation";
    }


    @GetMapping("/greeting")
    public String greeting(){

        return "greeting";
    }
}
