package io.lanu.travian.templates.buildings;


import io.lanu.travian.enums.EBuildings;
import io.lanu.travian.enums.EResource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceField extends BuildingBase{

    private EResource resource;
    private BigDecimal production;

    public ResourceField(EResource resource, BigDecimal production) {
        super();
        this.resource = resource;
        this.production = production;
        switch (this.resource){
            case CROP: this.name = EBuildings.CROPLAND.getName();
                break;
            case WOOD: this.name = EBuildings.WOODCUTTER.getName();
                break;
            case CLAY: this.name = EBuildings.CLAY_PIT.getName();
                break;
            case IRON: this.name = EBuildings.IRON_MINE.getName();
                break;
        }
    }


}
