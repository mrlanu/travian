package io.lanu.travian.game.entities;

import io.lanu.travian.enums.EBuildings;
import lombok.*;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BuildModel {
    private EBuildings kind;
    private int level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuildModel that = (BuildModel) o;
        return kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind);
    }
}
