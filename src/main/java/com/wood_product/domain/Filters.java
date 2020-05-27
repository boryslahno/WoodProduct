package com.wood_product.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "filters")
@Table(name = "filters")
public class Filters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(max = 50)
    private String filtername;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",nullable = false)
    private Categories category;
    @OneToMany(mappedBy = "filter",fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    private Set<FilterOptions> filterOptions;
    public Filters(){}

    public Filters(String filtername,Categories category){
        this.filtername=filtername;
        this.category=category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFiltername() {
        return filtername;
    }

    public void setFiltername(String filtername) {
        this.filtername = filtername;
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
}
