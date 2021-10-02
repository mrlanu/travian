package io.lanu.travian.game.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldView {
    protected int level;
    protected boolean underUpgrade;
    protected boolean ableToUpgrade;
}
