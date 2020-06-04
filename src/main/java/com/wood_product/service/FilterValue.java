package com.wood_product.service;

import java.util.List;

public class FilterValue {
    private String filterName;
    private List<String> value;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
