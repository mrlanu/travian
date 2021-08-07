package io.lanu.travian.game.entities;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.models.Field;
import io.lanu.travian.enums.VillageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Document("villages")
@NoArgsConstructor
@Slf4j
public class VillageEntity {
    @Id
    private String villageId;
    private String accountId;
    private int x;
    private int y;
    private VillageType villageType;
    private int population;
    private int culture;
    private List<Field> fields;
    private Map<Integer, String> buildings;
    private Map<Resource, BigDecimal> storage;
    private Map<Resource, BigDecimal> producePerHour;
    @LastModifiedDate
    private LocalDateTime modified;
}
