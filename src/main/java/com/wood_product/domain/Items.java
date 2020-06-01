package com.wood_product.domain;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "items")
public class Items {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 200)
    private String itemname;
    @NotNull
    private Integer count;
    @NotNull
    @Size(max = 1000)
    private String description;
    @Temporal(TemporalType.DATE)
    private Date addDate;
    @NotNull
    private Integer price;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",nullable = false)
    private Categories category;
    @OneToMany(mappedBy = "item",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<FilterOptions> filterOptions;
    @NotNull
    private String fileName;
    @Transient
    private Boolean size;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public Set<FilterOptions> getFilterOptions() {
        return filterOptions;
    }

    public void setFilterOptions(Set<FilterOptions> filterOptions) {
        this.filterOptions = filterOptions;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Boolean getSize() {
        return size;
    }

    public void setSize(Boolean size) {
        this.size = size;
    }
}
