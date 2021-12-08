package io.lanu.travian.game.entities;

import io.lanu.travian.enums.ENation;
import io.lanu.travian.game.models.responses.MilitaryUnitResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("military")
public class MilitaryUnitEntity {
    @Id
    private String id;
    private ENation nation;
    private boolean move;
    private String originVillageId;
    private String targetVillageId;
    private String currentLocationVillageId;
    private LocalDateTime arrivalTime;
    private int[] units;

    public MilitaryUnitResponse toMilitaryUnitResponse(String mission, VillageEntity origin,
                                                       VillageEntity target, String targetPlayerName, int expenses){
        return MilitaryUnitResponse.builder()
                .id(id)
                .nation(nation)
                .move(move)
                .mission(mission)
                .originVillageId(originVillageId)
                .originVillageName(origin.getName())
                .originVillageCoordinates(new int[]{origin.getX(), origin.getY()})
                .currentLocationVillageId(currentLocationVillageId)
                .targetVillageId(target.getVillageId())
                .targetVillageName(target.getName())
                .targetPlayerName(targetPlayerName)
                .targetVillageCoordinates(new int[]{target.getX(), target.getY()})
                .units(units)
                .arrivalTime(arrivalTime)
                .duration(240)
                .expensesPerHour(expenses)
                .build();
    }
}
