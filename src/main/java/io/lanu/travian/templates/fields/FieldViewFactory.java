package io.lanu.travian.templates.fields;

import io.lanu.travian.enums.Resource;
import io.lanu.travian.game.models.responses.FieldView;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldViewFactory {

    private static final Map<Resource, FieldTemplate> fields = Map.of(
            Resource.CROP, new FieldTemplate(Resource.CROP, Arrays.asList(70, 90, 70, 20),
                    1.67, 0, 1, 1420, 22, "Crop field"),
            Resource.CLAY, new FieldTemplate(Resource.CLAY, Arrays.asList(80, 40, 80, 50),
                    1.67, 2, 1, 1630, 22, "Clay field"),
            Resource.WOOD, new FieldTemplate(Resource.CLAY, Arrays.asList(40, 100, 50, 60),
                    1.67, 2, 1, 1750, 22, "Clay field"),
            Resource.IRON, new FieldTemplate(Resource.CLAY, Arrays.asList(100, 80, 30, 60),
                    1.67, 2, 1, 2310, 22, "Clay field")
    );

    private static final Integer[] productions = {0, 7, 13, 21, 31, 46, 70, 98, 140, 203, 280};

    public static FieldView get(Resource name, int level){
        final FieldTemplate template = fields.get(name);
        FieldView result = new FieldView();
        result.setFieldType(name);
        result.setLevel(level);
        result.setProduction(BigDecimal.valueOf(productions[level]));
        var resToNext = template.getCost().stream()
                .map(integer -> round(Math.pow(template.getK(), level) * integer, 5))
                .collect(Collectors.toList());
        var time = round((template.getTime() / 3) * Math.pow(1.6, level) - (1000d + level * 10) /3, 10);
        result.setResourcesToNextLevel(resToNext);
        result.setTimeToNextLevel(time);
        return result;
    }

    private static long round(double v, double n){
        return (long) (Math.round(v / n) * n);
    }
}
