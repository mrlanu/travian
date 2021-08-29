package io.lanu.travian.templates.entities.buildings;

import io.lanu.travian.enums.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/buildings")
public class BuildingsController {

    private final BuildingsRepository buildingsRepository;

    public BuildingsController(BuildingsRepository buildingsRepository) {
        this.buildingsRepository = buildingsRepository;
    }

    /*@GetMapping
    public String testBuilding(){
        var building1 = new BarrackBuilding(1, 1,
                Map.of(Resource.CROP, BigDecimal.valueOf(10)), 0);
        var building2 = new WarehouseBuilding(1, 3,
                Map.of(Resource.CROP, BigDecimal.valueOf(10)), 750);
        var saved = buildingsRepository.saveAll(Arrays.asList(building1, building2));
        var building3 = buildingsRepository.findById(saved.get(0).getId());
        System.out.println("Building >>>> " + building3.get());
        return "Done";
    }*/

    @PostMapping
    public ResponseEntity<GranaryBuilding> createBuilding(@RequestBody GranaryBuilding granaryBuilding){
        var building = buildingsRepository.save(granaryBuilding);
        return ResponseEntity.status(HttpStatus.CREATED).body(building);
    }
}
