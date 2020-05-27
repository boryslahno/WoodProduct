package com.wood_product.controller;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.wood_product.domain.*;
import com.wood_product.repos.*;
import com.wood_product.service.CategoryService;
import com.wood_product.service.FilterService;
import com.wood_product.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import java.util.*;

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
    //User management
    private Long IDUser;

    @GetMapping("/adminUserList")
    public String adminUserList(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
    }

    @PostMapping("/adminUserList")
    public String addAdmin(Users user, Model model) {
        Users userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.addAttribute("exists", "Користувач з такою електронною поштою вже існує");
        } else {
            user.setRoles(Collections.singleton(Role.Адміністратор));
            userRepository.save(user);
            model.addAttribute("addAdmin", "Адміністратор успішно доданий");
        }
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
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
    public String editUser(@RequestParam String name, @RequestParam String address, @RequestParam String phoneNumber, Model model) {
        Optional<Users> users = userRepository.findById(IDUser);
        Users user = users.get();
        Company company = user.getCompany();
        company.setName(name);
        company.setAddress(address);
        company.setPhoneNumber(phoneNumber);
        companyRepository.save(company);
        model.addAttribute("updateSeller", "Особиста інформація продавця успішно оновлена");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
    }

    @PostMapping("/adminUserList/shopper/edit")
    public String editUser(@RequestParam String name, @RequestParam String surname, @RequestParam String address, @RequestParam String phoneNumber, Model model) {
        Optional<Users> users = userRepository.findById(IDUser);
        Users user = users.get();
        PersonalInformation personalInformation = user.getPersonalInformation();
        personalInformation.setName(name);
        personalInformation.setSurname(surname);
        personalInformation.setAddress(address);
        personalInformation.setPhoneNumber(phoneNumber);
        personalInformationRepository.save(personalInformation);
        model.addAttribute("updateShopper", "Особиста інформація покупця успішно оновлена");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
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
    public String deleteUser(Model model) {
        userRepository.deleteById(IDUser);
        model.addAttribute("deleteSuccess", "Користувач успішно видалений");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("users", userService.loadAllUsers());
        return "adminUserList";
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
    public String addCategory(Categories cetegory, Model model) {
        Categories categories = categoryRepository.findByName(cetegory.getName());
        if (categories != null) {
            model.addAttribute("exists", "Категорія з такою назвою вже існує");
        } else {
            categoryRepository.save(cetegory);
            model.addAttribute("addCategory", "Категорія успішно додана");
        }
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories", categoryService.loadAllCategories());
        return "/adminCategoryList";
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
    public String editCategory(@RequestParam String name, Model model) {
        Categories categories = categoryRepository.findByName(name);
        if (categories != null) {
            model.addAttribute("exists", "Категорія з такою назвою вже існує");
        } else {
            Categories category = categoryRepository.findById(IDCategory).get();
            category.setName(name);
            categoryRepository.save(category);
            model.addAttribute("updateCategory", "Дані котегорії товару успішно оновлені");
            model.addAttribute("nameUser", userService.GetUserName());
            model.addAttribute("categories", categoryService.loadAllCategories());
        }
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories", categoryService.loadAllCategories());
        return "adminCategoryList";
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
    public String deleteCategory(Model model) {
        categoryRepository.deleteById(IDCategory);
        model.addAttribute("deleteSuccess", "Категорія товару успішно видалена");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("categories", categoryService.loadAllCategories());
        return "adminCategoryList";
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
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String repeatPassword, Model model) {
        Users user = userService.GetUserSecurityInfo();
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("adminPersonalInfo", userService.GetUserSecurityInfo());
        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("oldPasswordError", "Введено неправильний старий пароль");
            return "adminPersonalInfo";
        } else if (!newPassword.equals(repeatPassword)) {
            model.addAttribute("repeatPasswordError", "Паролі не співпадають");
            return "adminPersonalInfo";
        } else {
            user.setPassword(newPassword);
            userRepository.save(user);
            model.addAttribute("changePassword", "Пароль змінено успішно");
            return "adminPersonalInfo";
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
    public String addFilters(Model model, @RequestParam String filtername, @RequestParam String categoryName) {
        List<Filters> filterFromDB = filterRepository.findByFiltername(filtername);
        if (!filterFromDB.isEmpty()) {
            if (categoryRepository.findByFilternameAndName(filterRepository.findByFiltername(filtername).get(0).getFiltername().toString(), categoryName) == null) {
                Categories category = categoryRepository.findByName(categoryName);
                Filters filter = new Filters(filtername, category);
                filterRepository.save(filter);
                model.addAttribute("nameUser", userService.GetUserName());
                model.addAttribute("filters", filterService.loadAllFilters());
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("addFilter", "Фільтер успішно доданий");
            } else {
                model.addAttribute("nameUser", userService.GetUserName());
                model.addAttribute("filters", filterService.loadAllFilters());
                model.addAttribute("categories", categoryRepository.findAll());
                model.addAttribute("exists", "Даний фільтер вже належить цій категорії товарів");
            }
        } else {
            Categories category = categoryRepository.findByName(categoryName);
            Filters filter = new Filters(filtername, category);
            filterRepository.save(filter);
            model.addAttribute("nameUser", userService.GetUserName());
            model.addAttribute("filters", filterService.loadAllFilters());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("addFilter", "Фільтер успішно доданий");
        }
        return "adminFilterList";
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
    public String changeFilter(Model model, @RequestParam String filterName) {
        Filters filter = filterRepository.findById(IDfilter).get();
        if (categoryRepository.findByFilternameAndName(filterName, filter.getCategory().getName()) == null) {
            filter.setFiltername(filterName);
            filterRepository.save(filter);
            model.addAttribute("nameUser", userService.GetUserName());
            model.addAttribute("filters", filterService.loadAllFilters());
            model.addAttribute("updateFilter", "Дані фільтра успішно змінені");
        } else {
            model.addAttribute("nameUser", userService.GetUserName());
            model.addAttribute("filters", filterService.loadAllFilters());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("exists", "Даний фільтер вже належить цій категорії товарів");
        }
        return "adminFilterList";
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
    public String deleteFilter(Model model) {
        filterRepository.deleteById(IDfilter);
        model.addAttribute("deleteSuccess", "Фільтер товару успішно видалений");
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("filters", filterService.loadAllFilters());
        return "adminFilterList";
    }
}

