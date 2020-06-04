package com.wood_product.domain;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "categories")
@Table(name = "categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 50)
    private String name;
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE)
    private Set<Filters> filter;
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE)
    private Set<Items> items;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Filters> getFilter() {
        return filter;
    }

    public void setFilter(Set<Filters> filter) {
        this.filter = filter;
    }

    public Set<Items> getItems() {
        return items;
    }

    public void setItems(Set<Items> items) {
        this.items = items;
    }
}
