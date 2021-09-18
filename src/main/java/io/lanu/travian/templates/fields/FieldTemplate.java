package io.lanu.travian.templates.fields;

import io.lanu.travian.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FieldTemplate {
    private Resource name;
    private List<Integer> cost;
    private double k;
    private int cu;
    private int cp;
    private double time;
    private int maxLevel;
    private String description;
}
