package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.entities.VillageEntity;
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

    private int fieldPosition;

    public FieldUpgradeEvent(LocalDateTime executionTime, String villageId, int fieldPosition) {
        super(EventsType.UPGRADE_FIELD, villageId, executionTime);
        this.fieldPosition = fieldPosition;
    }

    @Override
    public void accept(VillageEntity villageEntity) {
        var field = villageEntity.getFields().get(fieldPosition);
        field.setLevel(field.getLevel() + 1);

        //villageEntityWrapper.getVillageEntity().getFields().set(fieldNew.getPosition(), fieldNew);
        //villageEntityWrapper.addGoodToProducePerHour(fieldNew.getFieldType(), fieldNew.getProduction().subtract(fieldOld.getProduction()));
    }
}
