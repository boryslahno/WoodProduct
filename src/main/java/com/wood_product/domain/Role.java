package com.wood_product.domain;

import org.springframework.security.core.GrantedAuthority;

public enum  Role implements GrantedAuthority {
    Покупець,Продавець,Адміністратор;

    @Override
    public String getAuthority() {
        return name();
    }
}
