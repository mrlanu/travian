package io.lanu.travian.game.models.responses;

import io.lanu.travian.enums.EResource;
import io.lanu.travian.game.entities.events.BuildIEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class FieldView {
    protected int position;
    protected int level;
    protected boolean underUpgrade;
    protected boolean ableToUpgrade;

    public abstract void setAbleToUpgrade(Map<EResource, BigDecimal> storage);

    public abstract void setUnderUpgrade(List<BuildIEvent> eventList);
}
