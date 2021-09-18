package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.game.models.VillageEntityWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Document("events")
@TypeAlias("fieldUpgradeEvent")
public class FieldUpgradeEvent extends Event {

    private Field fieldOld;
    private Field fieldNew;

    public FieldUpgradeEvent(LocalDateTime executionTime, String villageId, Field fieldNew, Field fieldOld) {
        super(EventsType.UPGRADE_FIELD, villageId, executionTime);
        this.fieldNew = fieldNew;
        this.fieldOld = fieldOld;
    }

    @Override
    public void accept(VillageEntityWrapper villageEntityWrapper) {
        //villageEntityWrapper.getVillageEntity().getFields().set(fieldNew.getPosition(), fieldNew);
        //villageEntityWrapper.addGoodToProducePerHour(fieldNew.getFieldType(), fieldNew.getProduction().subtract(fieldOld.getProduction()));
    }
}
