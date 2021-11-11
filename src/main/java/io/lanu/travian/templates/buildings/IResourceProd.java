package io.lanu.travian.templates.buildings;

import io.lanu.travian.enums.EResource;

import java.math.BigDecimal;

public interface IResourceProd {
    default EResource getResource(){
        return null;
    }
    default BigDecimal getProduction(){
        return null;
    }
}
