package com.wood_product.controller;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wood_product.domain.*;
import com.wood_product.repos.*;
import com.wood_product.service.CategoryService;
import com.wood_product.service.FilterService;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

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
    @Autowired
    private FilterRepository filterRepository;
    @Autowired
    private FilterService filterService;
    @Autowired
    private  ItemRepository itemRepository;
    @Autowired
    private  FilterOptionsRepository filterOptionsRepository;
    //User management
    private Long IDUser;

    @GetMapping("/adminUserList")
    public String adminUserList(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
    }

    @PostMapping("/adminUserList")
    public String addAdmin(Users user, RedirectAttributes redirectAttributes) {
        Users userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            redirectAttributes.addFlashAttribute("exists", "Користувач з такою електронною поштою вже існує");
        } else {
            user.setRoles(Collections.singleton(Role.Адміністратор));
            Date currentDate=new Date();
            user.setRegisterDate(currentDate);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("addAdmin", "Адміністратор успішно доданий");
        }
        return "redirect:/adminUserList";
    }

    @GetMapping("/adminUserList/users")
    public String reviewUser(@RequestParam Long userID, Model model) {
        IDUser = userID;
        String role = userService.GetUserRole(userID);
        if (role == "Продавець") {
            model.addAttribute("sellerPerInfo", userService.loadCompanyInfo(userID));
            model.addAttribute("shopperPerInf", null);
            model.addAttribute("adminPerInfo", null);
        } else if (role == "Покупець") {
            model.addAttribute("sellerPerInfo", null);
            model.addAttribute("shopperPerInf", userService.loadPersonalInfo(userID));
            model.addAttribute("adminPerInfo", null);
        } else {
            model.addAttribute("sellerPerInfo", null);
            model.addAttribute("shopperPerInf", null);
            model.addAttribute("adminPerInfo", "Особиста інформація відсутня");
        }
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
    }

    @PostMapping("/adminUserList/seller/edit")
    public String editUser(@RequestParam String name, @RequestParam String address,
                           @RequestParam String phoneNumber, RedirectAttributes redirectAttributes) {
        Optional<Users> users = userRepository.findById(IDUser);
        Users user = users.get();
        Company company = user.getCompany();
        company.setName(name);
        company.setAddress(address);
        company.setPhoneNumber(phoneNumber);
        companyRepository.save(company);
        redirectAttributes.addFlashAttribute("updateSeller", "Особиста інформація продавця успішно оновлена");
        return "redirect:/adminUserList";
    }

    @PostMapping("/adminUserList/shopper/edit")
    public String editUser(@RequestParam String name, @RequestParam String surname,
                           @RequestParam String address, @RequestParam String phoneNumber, RedirectAttributes redirectAttributes) {
        Optional<Users> users = userRepository.findById(IDUser);
        Users user = users.get();
        PersonalInformation personalInformation = user.getPersonalInformation();
        personalInformation.setName(name);
        personalInformation.setSurname(surname);
        personalInformation.setAddress(address);
        personalInformation.setPhoneNumber(phoneNumber);
        personalInformationRepository.save(personalInformation);
        redirectAttributes.addFlashAttribute("updateShopper", "Особиста інформація покупця успішно оновлена");
        return "redirect:/adminUserList";
    }

    @GetMapping("/adminUserList/delete/user")
    public String deleteUser(@RequestParam Long userID, Model model) {
        IDUser = userID;
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        model.addAttribute("deleteUser", "Ви дійсно бажаєте видадалити цього користувача?");
        return "adminUserList";
    }

    @PostMapping("/adminUserList/delete/user")
    public String deleteUser(RedirectAttributes redirectAttributes) {
        userRepository.deleteById(IDUser);
        redirectAttributes.addFlashAttribute("deleteSuccess", "Користувач успішно видалений");
        return "redirect:/adminUserList";
    }

    //Category product management
    private Long IDCategory;

    @GetMapping("/adminCategoryList")
    public String adminCategoryList(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories", categoryService.loadAllCategories());
        return "adminCategoryList";
    }

    @PostMapping("/adminCategoryList")
    public String addCategory(Categories cetegory, RedirectAttributes redirectAttributes) {
        Categories categories = categoryRepository.findByName(cetegory.getName());
        if (categories != null) {
            redirectAttributes.addFlashAttribute("exists", "Категорія з такою назвою вже існує");
        } else {
            categoryRepository.save(cetegory);
            redirectAttributes.addFlashAttribute("addCategory", "Категорія успішно додана");
        }
        return "redirect:/adminCategoryList";
    }

    @GetMapping("/adminCategoryList/edit/category")
    public String reviewCategory(@RequestParam Long categoryID, Model model) {
        IDCategory = categoryID;
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories", categoryService.loadAllCategories());
        model.addAttribute("categoryEdit", categoryRepository.findById(categoryID).get());
        return "adminCategoryList";
    }

    @PostMapping("/adminCategoryList/edit/category")
    public String editCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        Categories categories = categoryRepository.findByName(name);
        if (categories != null) {
            redirectAttributes.addFlashAttribute("exists", "Категорія з такою назвою вже існує");
        } else {
            Categories category = categoryRepository.findById(IDCategory).get();
            category.setName(name);
            categoryRepository.save(category);
            redirectAttributes.addFlashAttribute("updateCategory", "Дані котегорії товару успішно оновлені");
        }
        return "redirect:/adminCategoryList";
    }

    @GetMapping("/adminCategoryList/delete/category")
    public String deleteCategory(@RequestParam Long categoryID, Model model) {
        IDCategory = categoryID;
        model.addAttribute("deleteCategory", "Ви дійсно бажаєте видалити цю категорію товару?");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories", categoryService.loadAllCategories());
        return "adminCategoryList";
    }

    @PostMapping("/adminCategoryList/delete/category")
    public String deleteCategory(RedirectAttributes redirectAttributes) {
        categoryRepository.deleteById(IDCategory);
        redirectAttributes.addFlashAttribute("deleteSuccess", "Категорія товару успішно видалена");
        return "redirect:/adminCategoryList";
    }

    //Personal information management
    @GetMapping("/adminPersonalInformation")
    public String personalInfo(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("adminPersonalInfo", userService.GetUserSecurityInfo());
        return "adminPersonalInfo";
    }

    @GetMapping("/adminPersonalInformation/changeEmail")
    public String changeEmail(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("adminPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("adminChangeEmail", userService.GetUserSecurityInfo());
        return "adminPersonalInfo";
    }

    @PostMapping("/adminPersonalInformation/changeEmail")
    public String changeEmail(@RequestParam String username, Model model) {
        Users user = userService.GetUserSecurityInfo();
        Users userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null) {
            model.addAttribute("exists", "Користувач з такою електронною поштою вже існує");
            model.addAttribute("nameUser", userService.GetUserName());
            model.addAttribute("adminPersonalInfo", userService.GetUserSecurityInfo());
            return "adminPersonalInfo";
        } else {
            user.setUsername(username);
            userRepository.save(user);
            return "redirect:/login";
        }
    }

    @GetMapping("/adminPersonalInformation/changePassword")
    public String changePassword(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("adminPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("adminChangePassword", true);
        return "adminPersonalInfo";
    }

    @PostMapping("/adminPersonalInformation/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                 @RequestParam String repeatPassword, RedirectAttributes redirectAttributes) {
        Users user = userService.GetUserSecurityInfo();
        if (!oldPassword.equals(user.getPassword())) {
            redirectAttributes.addFlashAttribute("oldPasswordError", "Введено неправильний старий пароль");
            return "redirect:/adminPersonalInformation";
        } else if (!newPassword.equals(repeatPassword)) {
            redirectAttributes.addFlashAttribute("repeatPasswordError", "Паролі не співпадають");
            return "redirect:/adminPersonalInformation";
        } else {
            user.setPassword(newPassword);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("changePassword", "Пароль змінено успішно");
            return "redirect:/adminPersonalInformation";
        }
    }

    //Filter product management
    private Long IDfilter;

    @GetMapping("/adminFilterList")
    public String adminFilterList(Model model) {
        Iterable<Filters> filters = filterService.loadAllFilters();
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("filters", filters);
        model.addAttribute("categories", categoryRepository.findAll());
        return "adminFilterList";
    }

    @PostMapping("/adminFilterList")
    public String addFilters(RedirectAttributes redirectAttributes, @RequestParam String filtername, @RequestParam String categoryName) {
        List<Filters> filterFromDB = filterRepository.findByFiltername(filtername);
        if (!filterFromDB.isEmpty()) {
            if (categoryRepository.findByFilternameAndName(filterRepository.findByFiltername(filtername).get(0).getFiltername(), categoryName) == null) {
                Categories category = categoryRepository.findByName(categoryName);
                Filters filter = new Filters(filtername, category);
                filterRepository.save(filter);
                redirectAttributes.addFlashAttribute("addFilter", "Фільтер успішно доданий");
            } else {
                redirectAttributes.addFlashAttribute("exists", "Даний фільтер вже належить цій категорії товарів");
            }
        } else {
            Categories category = categoryRepository.findByName(categoryName);
            Filters filter = new Filters(filtername, category);
            filterRepository.save(filter);
            redirectAttributes.addFlashAttribute("addFilter", "Фільтер успішно доданий");
        }
        return "redirect:/adminFilterList";
    }

    @GetMapping("/adminFilterList/edit/filter")
    public String changeFilter(Model model, @RequestParam Long filterID) {
        IDfilter = filterID;
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("filters", filterService.loadAllFilters());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("filterEdit", filterRepository.findById(filterID).get());
        return "adminFilterList";
    }

    @PostMapping("/adminFilterList/edit/filter")
    public String changeFilter(RedirectAttributes redirectAttributes, @RequestParam String filterName) {
        Filters filter = filterRepository.findById(IDfilter).get();
        if (categoryRepository.findByFilternameAndName(filterName, filter.getCategory().getName()) == null) {
            filter.setFiltername(filterName);
            filterRepository.save(filter);
            redirectAttributes.addFlashAttribute("updateFilter", "Дані фільтра успішно змінені");
        } else {
            redirectAttributes.addFlashAttribute("exists", "Даний фільтер вже належить цій категорії товарів");
        }
        return "redirect:/adminFilterList";
    }

    @GetMapping("/adminFilterList/delete/filter")
    public String deleteFilter(@RequestParam Long filterID, Model model) {
        IDfilter = filterID;
        model.addAttribute("deleteFilter", "Ви дійсно бажаєте видалити цей фільтер товару?");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("filters", filterService.loadAllFilters());
        return "adminFilterList";
    }

    @PostMapping("/adminFilterList/delete/filter")
    public String deleteFilter(RedirectAttributes redirectAttributes) {
        filterRepository.deleteById(IDfilter);
        redirectAttributes.addFlashAttribute("deleteSuccess", "Фільтер товару успішно видалений");
        return "redirect:/adminFilterList";
    }

    //Items management
    private Long IDitem;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/adminItemList")
    public String sellerItemInStockList(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items", itemRepository.findAll());
        return "adminItemList";
    }

    @GetMapping("/admin/deleteProduct")
    public String deleteProduct(RedirectAttributes redirectAttributes, @RequestParam Long itemID) {
        IDitem = itemID;
        redirectAttributes.addFlashAttribute("deleteItem", "Ви дійсно бажаєте видалити цей товар?");
        return "redirect:/adminItemList";
    }
    @PostMapping("/admin/deleteProduct")
    public String deleteProduct(RedirectAttributes redirectAttributes) {
        Items item=itemRepository.findById(IDitem).get();
        File file=new File(uploadPath+item.getFileName());
        itemRepository.deleteById(IDitem);
        file.delete();
        redirectAttributes.addFlashAttribute("deleteSuccess", "Товар успішно видалений");
        return "redirect:/adminItemList";
    }

    @GetMapping("/admin/editProduct")
    public String editProduct(RedirectAttributes redirectAttributes, @RequestParam Long itemID){
        IDitem = itemID;
        redirectAttributes.addFlashAttribute("item",itemRepository.findById(itemID).get());
        redirectAttributes.addFlashAttribute("itemEdit", true);
        return "redirect:/adminItemList";
    }

    @Transactional
    @PostMapping("/admin/editProduct")
    public String editProduct(RedirectAttributes redirectAttributes, @RequestParam("filters[]") String[] filters,
                              @RequestParam("filtersOptionID[]") Long[] filtersID,
                              @RequestParam String itemname, @RequestParam Integer count,
                              @RequestParam String description, @RequestParam Integer price) {
        Items item=itemRepository.findById(IDitem).get();
        item.setItemname(itemname);
        item.setCount(count);
        item.setDescription(description);
        item.setPrice(price);
        for (int i = 0; i < filtersID.length; ++i) {
            FilterOptions filterOption = filterOptionsRepository.findById(filtersID[i]).get();
            filterOption.setValue(filters[i]);
            filterOptionsRepository.save(filterOption);
        }
        redirectAttributes.addFlashAttribute("updateSuccess","Інформація про товар успішно змінена");
        return "redirect:/adminItemList";
    }

    @GetMapping("/adminViewProduct")
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
        return "adminViewProduct";
    }
}

