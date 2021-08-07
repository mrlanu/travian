package io.lanu.travian.game.services;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.entities.VillageEntity;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Slf4j
public class VillageGeneratorFacade {

    private final VillageEntity villageEntity;

    public VillageGeneratorFacade(VillageEntity villageEntity) {
        this.villageEntity = villageEntity;
    }

    public void generateVillage(){
        /*villageEntity.setProducePerHour(sumProducePerHour());*/
        calculateProducedGoods(villageEntity.getModified(), LocalDateTime.now());
    }

    private void calculateProducedGoods(LocalDateTime lastModified, LocalDateTime untilTime){

        long durationFromLastModified = ChronoUnit.MILLIS.between(lastModified, untilTime);
        MathContext mc = new MathContext(3);
        // here is a formula for the productions counting
        // new BigDecimal((durationFromLastModified * (double) producePerHour.get(FieldType.WOOD)) / 3600000L, mc)
        BigDecimal divide = BigDecimal.valueOf(durationFromLastModified)
                .divide(BigDecimal.valueOf(3600000L), mc);

        BigDecimal woodProduced =
                villageEntity.getProducePerHour().get(Resource.WOOD)
                        .multiply(divide);
        BigDecimal clayProduced =
                villageEntity.getProducePerHour().get(Resource.CLAY)
                        .multiply(divide);
        BigDecimal ironProduced =
                villageEntity.getProducePerHour().get(Resource.IRON)
                        .multiply(divide);
        BigDecimal cropProduced =
                villageEntity.getProducePerHour().get(Resource.CROP)
                        .multiply(divide);

        addGoods(Map.of(Resource.WOOD, woodProduced, Resource.CLAY, clayProduced,
                Resource.IRON, ironProduced, Resource.CROP, cropProduced));

        log.info("Produced resources added to the Warehouse.");
    }

    /*private Map<Resource, BigDecimal> sumProducePerHour(){
        return villageEntity.getFields().stream()
                .collect(Collectors.groupingBy(Field::getFieldType,
                        Collectors.reducing(BigDecimal.ZERO, Field::getProduction, BigDecimal::add)));
    }*/

    private void addGoods(Map<Resource, BigDecimal> producedResources){
        var st = villageEntity.getStorage();
        st.forEach((k, v) -> st.put(k, st.get(k).add(producedResources.get(k))));
    }

}
