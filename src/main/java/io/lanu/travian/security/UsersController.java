package io.lanu.travian.security;

import io.lanu.travian.game.models.responses.ShortVillageInfo;
import io.lanu.travian.game.services.SettlementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService usersService;
    private final SettlementService settlementService;

    public UsersController(UsersService usersService, SettlementService settlementService) {
        this.usersService = usersService;
        this.settlementService = settlementService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserEntity> registerUser(@RequestBody AuthRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.registerUser(request));
    }

    @GetMapping("/users/{userId}/villages")
    public ResponseEntity<List<ShortVillageInfo>> getAllVillagesIdByUserId(@PathVariable String userId){
        return ResponseEntity.status(HttpStatus.OK).body(settlementService.getAllVillagesByUserId(userId));
    }
}
