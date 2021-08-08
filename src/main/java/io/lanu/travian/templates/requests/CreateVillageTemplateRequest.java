package io.lanu.travian.templates.requests;

import io.lanu.travian.templates.entities.VillageTemplate;
import lombok.Data;

@Data
public class CreateVillageTemplateRequest {
    private VillageTemplate villageTemplate;
}
