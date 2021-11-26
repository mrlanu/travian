package io.lanu.travian.templates.buildings;


import io.lanu.travian.enums.EBuilding;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class Marketplace extends BuildingBase{
    private Integer traders;

    public Marketplace(Integer traders) {
        super();
        this.name = EBuilding.MARKETPLACE.getName();
        this.traders = traders;
    }
}
