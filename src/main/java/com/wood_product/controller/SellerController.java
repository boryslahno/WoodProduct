package com.wood_product.controller;

import com.wood_product.domain.Company;
import com.wood_product.domain.FilterOptions;
import com.wood_product.domain.Items;
import com.wood_product.domain.Users;
import com.wood_product.repos.*;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
public class SellerController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FilterOptionsRepository filterOptionsRepository;
    @Autowired
    private FilterRepository filterRepository;
    //Personal information management
    @GetMapping("/sellerPersonalInformation")
    public String personalInfo(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo",userService.loadCompanyInfo());
        return "sellerPersonalInformation";
    }

    @GetMapping("/sellerPersonalInformation/changeEmail")
    public String changeEmail(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo",userService.loadCompanyInfo());
        model.addAttribute("sellerChangeEmail",userService.GetUserSecurityInfo());
        return "sellerPersonalInformation";
    }

    @PostMapping("/sellerPersonalInformation/changeEmail")
    public String changeEmail(@RequestParam String username, Model model) {
        Users user = userService.GetUserSecurityInfo();
        Users userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null) {
            model.addAttribute("exists", "Користувач з такою електронною поштою вже існує");
            model.addAttribute("nameUser", userService.GetUserName());
            model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
            model.addAttribute("companyInfo",userService.loadCompanyInfo());
            return "sellerPersonalInformation";
        } else {
            user.setUsername(username);
            userRepository.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/sellerPersonalInformation/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo",userService.loadCompanyInfo());
        model.addAttribute("sellerChangePassword", true);
        return "sellerPersonalInformation";
    }

    @PostMapping("/sellerPersonalInformation/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String repeatPassword, Model model) {
        Users user = userService.GetUserSecurityInfo();
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo",userService.loadCompanyInfo());
        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("oldPasswordError", "Введено неправильний старий пароль");
            return "sellerPersonalInformation";
        } else if (!newPassword.equals(repeatPassword)) {
            model.addAttribute("repeatPasswordError", "Паролі не співпадають");
            return "sellerPersonalInformation";
        } else {
            user.setPassword(newPassword);
            userRepository.save(user);
            model.addAttribute("changePassword", "Пароль змінено успішно");
            return "sellerPersonalInformation";
        }
    }

    @GetMapping("/sellerPersonalInformation/changeCompanyInformation")
    public String changeCompanyInformation(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo",userService.loadCompanyInfo());
        model.addAttribute("sellerChangeCompany",userService.loadCompanyInfo());
        return "sellerPersonalInformation";
    }

    @PostMapping("/sellerPersonalInformation/changeCompanyInformation")
    public String changeCompanyInformation(Model model,@RequestParam String companyName,@RequestParam String companyAddress,@RequestParam String companyPhone){
        Company company=userService.loadCompanyInfo();
        company.setName(companyName);
        company.setAddress(companyAddress);
        company.setPhoneNumber(companyPhone);
        companyRepository.save(company);
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo",userService.loadCompanyInfo());
        model.addAttribute("changeCompanyInfo","Особиста інформація успішно оновлена");
        return "sellerPersonalInformation";
    }

    //Products in stock management
    @GetMapping("/sellerProductInStock")
    public String sellerItemInStockList(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items",itemRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "sellerProductInStock";
    }
    @PostMapping("/sellerProductInStock")
    public String selectCategoryItem(Model model,@RequestParam String categoryName){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items",itemRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("addItem",categoryName);
        model.addAttribute("filters",categoryRepository.findByName(categoryName).getFilter());
        return "sellerProductInStock";
    }

    @GetMapping("/addProduct")
    public String addItems(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items",itemRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "sellerProductInStock";
    }
    @Transactional
    @PostMapping("/addProduct")
    public String addItems(Model model, @RequestParam("file") MultipartFile file,
                           @RequestParam("filters[]") String[]filters,
                           @RequestParam("filtersName[]") String[]filtersName,
                           @RequestParam String itemname,@RequestParam Integer count,
                           @RequestParam String description,@RequestParam Integer price,
                           @RequestParam String name) throws IOException {
        Items item=new Items();
        item.setItemname(itemname);
        item.setCount(count);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(categoryRepository.findByName(name));
        if(file!=null){
            File uploadDir=new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String uuidFile= UUID.randomUUID().toString();
            String resultFilename=uuidFile+"."+file.getOriginalFilename();
            file.transferTo(new File(uploadPath+resultFilename));

            item.setFileName(resultFilename);
        }
        Set<FilterOptions> filterOptions=new HashSet<FilterOptions>();
        itemRepository.save(item);
        for(int i=0;i<filtersName.length;++i){
            FilterOptions filterOption=new FilterOptions(filters[i],item,
                    filterRepository.findById(filterRepository.findByFilternameAndName(filtersName[i],name)).get());
            filterOptions.add(filterOption);
            filterOptionsRepository.save(filterOption);
        }
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items",itemRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("addItem",false);
        return "redirect://sellerProductInStock";
    }
}
