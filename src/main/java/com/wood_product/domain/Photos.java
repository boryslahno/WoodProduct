package com.wood_product.domain;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "photos")
public class Photos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
