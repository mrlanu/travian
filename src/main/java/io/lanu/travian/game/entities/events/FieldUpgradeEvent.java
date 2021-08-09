package io.lanu.travian.game.entities.events;

import io.lanu.travian.enums.EventsType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.Field;
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
    private boolean paid;

    public FieldUpgradeEvent(LocalDateTime executionTime, String villageId, Field fieldNew, Field fieldOld, boolean paid) {
        super(EventsType.UPGRADE_FIELD, villageId, executionTime);
        this.fieldNew = fieldNew;
        this.fieldOld = fieldOld;
        this.paid = paid;
    }

    @Override
    public void accept(VillageEntity villageEntity) {
        villageEntity.getFields().set(fieldNew.getPosition(), fieldNew);
        addGoodToProducePerHour(villageEntity, fieldNew.getFieldType(), fieldNew.getProduction().subtract(fieldOld.getProduction()));
    }
}
