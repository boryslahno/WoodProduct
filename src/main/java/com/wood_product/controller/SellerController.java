package com.wood_product.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.xml.bind.v2.model.core.ID;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
    @Autowired
    private ShoppingRepository shoppingRepository;
    @Autowired
    private CommentsRepository commentsRepository;

    private Long IDitem;

    //Personal information management
    @GetMapping("/sellerPersonalInformation")
    public String personalInfo(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo", userService.loadCompanyInfo());
        return "sellerPersonalInformation";
    }

    @GetMapping("/sellerPersonalInformation/changeEmail")
    public String changeEmail(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("sellerChangeEmail", userService.GetUserSecurityInfo());
        return "redirect:/sellerPersonalInformation";
    }

    @PostMapping("/sellerPersonalInformation/changeEmail")
    public String changeEmail(@RequestParam String username, RedirectAttributes redirectAttributes) {
        Users user = userService.GetUserSecurityInfo();
        Users userFromDB = userRepository.findByUsername(username);
        if (userFromDB != null) {
            redirectAttributes.addFlashAttribute("exists", "Користувач з такою електронною поштою вже існує");
            return "redirect:/sellerPersonalInformation";
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
        model.addAttribute("companyInfo", userService.loadCompanyInfo());
        model.addAttribute("sellerChangePassword", true);
        return "sellerPersonalInformation";
    }

    @PostMapping("/sellerPersonalInformation/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                 @RequestParam String repeatPassword, RedirectAttributes redirectAttributes) {
        Users user = userService.GetUserSecurityInfo();
        if (!oldPassword.equals(user.getPassword())) {
            redirectAttributes.addFlashAttribute("oldPasswordError", "Введено неправильний старий пароль");
            return "redirect:/sellerPersonalInformation";
        } else if (!newPassword.equals(repeatPassword)) {
            redirectAttributes.addFlashAttribute("repeatPasswordError", "Паролі не співпадають");
            return "redirect:/sellerPersonalInformation";
        } else {
            user.setPassword(newPassword);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("changePassword", "Пароль змінено успішно");
            return "redirect:/sellerPersonalInformation";
        }
    }

    @GetMapping("/sellerPersonalInformation/changeCompanyInformation")
    public String changeCompanyInformation(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("sellerPersonalInfo", userService.GetUserSecurityInfo());
        model.addAttribute("companyInfo", userService.loadCompanyInfo());
        model.addAttribute("sellerChangeCompany", userService.loadCompanyInfo());
        return "sellerPersonalInformation";
    }

    @PostMapping("/sellerPersonalInformation/changeCompanyInformation")
    public String changeCompanyInformation(RedirectAttributes redirectAttributes, @RequestParam String companyName, @RequestParam String companyAddress, @RequestParam String companyPhone) {
        Company company = userService.loadCompanyInfo();
        company.setName(companyName);
        company.setAddress(companyAddress);
        company.setPhoneNumber(companyPhone);
        companyRepository.save(company);
        redirectAttributes.addFlashAttribute("changeCompanyInfo", "Особиста інформація успішно оновлена");
        return "sellerPersonalInformation";
    }

    //Products in stock management
    @GetMapping("/sellerProductInStock")
    public String sellerItemInStockList(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items", itemRepository.findByUser(userService.currentUser()));
        model.addAttribute("categories", categoryRepository.findAll());
        return "sellerProductInStock";
    }

    @PostMapping("/sellerProductInStock")
    public String selectCategoryItem(Model model, RedirectAttributes redirectAttributes, @RequestParam String categoryName) {
        redirectAttributes.addFlashAttribute("addItem", categoryName);
        redirectAttributes.addFlashAttribute("filters", categoryRepository.findByName(categoryName).getFilter());
        return "redirect:/sellerProductInStock";
    }

    @GetMapping("/addProduct")
    public String addItems(Model model) {
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("items", itemRepository.findByUser(userService.currentUser()));
        model.addAttribute("categories", categoryRepository.findAll());
        return "sellerProductInStock";
    }

    @Transactional
    @PostMapping("/addProduct")
    public String addItems(Model model, RedirectAttributes redirectAttributes,
                           @RequestParam("file") MultipartFile file,
                           @RequestParam("filters[]") String[] filters,
                           @RequestParam("filtersName[]") String[] filtersName,
                           @RequestParam String itemname, @RequestParam Integer count,
                           @RequestParam String description, @RequestParam Integer price,
                           @RequestParam String name) throws IOException {
        Items item = new Items();
        item.setUser(userService.currentUser());
        item.setItemname(itemname);
        item.setCount(count);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(categoryRepository.findByName(name));
        if (file != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + resultFilename));

            item.setFileName(resultFilename);
        }
        Set<FilterOptions> filterOptions = new HashSet<FilterOptions>();
        Date currentDate=new Date();
        item.setAddDate(currentDate);
        itemRepository.save(item);
        for (int i = 0; i < filtersName.length; ++i) {
            FilterOptions filterOption = new FilterOptions(filters[i], item,
                    filterRepository.findById(filterRepository.findByFilternameAndName(filtersName[i], name)).get());
            filterOptions.add(filterOption);
            filterOptionsRepository.save(filterOption);
        }
        redirectAttributes.addFlashAttribute("addItemSuccess", "Товар успішно доданий");
        return "redirect:/sellerProductInStock";
    }

    @GetMapping("/deleteProduct")
    public String deleteProduct(RedirectAttributes redirectAttributes, @RequestParam Long itemID) {
        IDitem = itemID;
        redirectAttributes.addFlashAttribute("deleteItem", "Ви дійсно бажаєте видалити цей товар?");
        return "redirect:/sellerProductInStock";
    }
    @PostMapping("/deleteProduct")
    public String deleteProduct(RedirectAttributes redirectAttributes) {
        Items item=itemRepository.findById(IDitem).get();
        File file=new File(uploadPath+item.getFileName());
        itemRepository.deleteById(IDitem);
        file.delete();
        redirectAttributes.addFlashAttribute("deleteSuccess", "Товар успішно видалений");
        return "redirect:/sellerProductInStock";
    }

    @GetMapping("/editProduct")
    public String editProduct(RedirectAttributes redirectAttributes, @RequestParam Long itemID){
        IDitem = itemID;
        redirectAttributes.addFlashAttribute("item",itemRepository.findById(itemID).get());
        redirectAttributes.addFlashAttribute("itemEdit", true);
        return "redirect:/sellerProductInStock";
    }

    @Transactional
    @PostMapping("/editProduct")
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
        return "redirect:/sellerProductInStock";
    }

    @GetMapping("/sellerShowProduct")
    public String showProduct(@RequestParam Long itemID){
        IDitem=itemID;
        return "redirect:/sellerViewProduct";
    }

    @GetMapping("/sellerViewProduct")
    public String viewProduct(Model model){
        Items item=itemRepository.findById(IDitem).get();
        model.addAttribute("item",item);
        model.addAttribute("nameUser", userService.GetUserName());
        Image image=new ImageIcon(uploadPath+item.getFileName()).getImage();
        if(image.getHeight(null)>=image.getWidth(null)){
            model.addAttribute("heightSize",100);
        }else{
            model.addAttribute("widthSize",100);
        }
        model.addAttribute("comments",commentsRepository.findByItem(itemRepository.findById(IDitem).get()));
        return "sellerViewProduct";
    }

    @GetMapping("/sellerSoldProducts")
    public String viewSoldProducts(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("soldItems",shoppingRepository.findByOwner(userService.currentUser()));
        return "sellerSoldProducts";
    }

    @GetMapping("/countSold")
    public String countSold(Model model,@RequestParam Date startData,@RequestParam Date endData){
        model.addAttribute("nameUser", userService.GetUserName());
        model.addAttribute("soldItems",shoppingRepository.findByOwner(userService.currentUser()));

        return "sellerSoldProducts";
    }

    //Seller statistic
    @GetMapping("/sellerStatistic")
    public String Statistic(Model model){
        model.addAttribute("nameUser", userService.GetUserName());
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate=new Date();
        try {
            model.addAttribute("totalSum",shoppingRepository.findSumSold(userService.currentUser(),currentDate,currentDate)*2);
        }catch (NullPointerException ex){
            model.addAttribute("totalSum",0);
        }
        Integer totalQuantity=shoppingRepository.findCountSold(userService.currentUser(),currentDate,currentDate);
        if(totalQuantity==null){
            totalQuantity=0;
        }
        List<Map<String,Object>> topItems=shoppingRepository.findTopItems(userService.currentUser(),currentDate,currentDate);
        if(topItems.size()>5){
            model.addAttribute("topItems",topItems.subList(0,6));
        }else
        {
            model.addAttribute("topItems",topItems);
        }
        model.addAttribute("totalQuantity",totalQuantity);
        model.addAttribute("categoryQuantity",shoppingRepository.findCategoryCountSold(userService.currentUser(),currentDate,currentDate));
        return "sellerStatistic";
    }

    @GetMapping("/viewStatistic")
    public String viewStatistic(Model model,@RequestParam String startData,@RequestParam String endData) throws Exception{
        model.addAttribute("nameUser", userService.GetUserName());
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        Date start=format.parse(startData);
        Date end=format.parse(endData);
        try {
            model.addAttribute("totalSum",shoppingRepository.findSumSold(userService.currentUser(),start,end)*2);
        }catch (NullPointerException ex){
            model.addAttribute("totalSum",0);
        }
        Integer totalQuantity=shoppingRepository.findCountSold(userService.currentUser(),start,end);
        if(totalQuantity==null){
            totalQuantity=0;
        }
        List<Map<String,Object>> topItems=shoppingRepository.findTopItems(userService.currentUser(),start,end);
        if(topItems.size()>5){
            model.addAttribute("topItems",topItems.subList(0,5));
        }else
        {
            model.addAttribute("topItems",topItems);
        }
        model.addAttribute("totalQuantity",totalQuantity);
        model.addAttribute("categoryQuantity",shoppingRepository.findCategoryCountSold(userService.currentUser(),start,end));
        return "sellerStatistic";
    }
}
