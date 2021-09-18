package io.lanu.travian.game.entities;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldEntity {
    private Resource type;
    private int level;
}
