package io.lanu.travian.templates.entities;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document("fields-templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldTemplate {
    private int position;
    private Resource fieldType;
    private int level;
    private int production;
    private Map<Resource, Integer> resourcesToNextLevel;
    private boolean underUpgrade;
    private boolean ableToUpgrade;
}
