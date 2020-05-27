package com.wood_product.domain;

import org.hibernate.annotations.JoinColumnOrFormula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "filter_options")
public class FilterOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 200)
    private String value;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id",nullable = false)
    private Items item;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "filter_id",nullable = false)
    private Filters filter;

    public FilterOptions(){}

    public FilterOptions(String value,Items item,Filters filter){
        this.value=value;
        this.item=item;
        this.filter=filter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    public Filters getFilter() {
        return filter;
    }

    public void setFilter(Filters filter) {
        this.filter = filter;
    }
}
