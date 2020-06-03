package com.wood_product.domain;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;

@Entity(name = "shopping")
@Table(name = "shopping")
public class Shopping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    private Integer total;
    @Temporal(TemporalType.DATE)
    private Date shopDate;
    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "item_id",nullable = false)
    private Items item;
    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @JoinColumn(name = "user_id",nullable = false)
    private Users user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Date getShopDate() {
        return shopDate;
    }

    public void setShopDate(Date shopDate) {
        this.shopDate = shopDate;
    }

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
