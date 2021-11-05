package io.lanu.travian.security;

import io.lanu.travian.game.services.VillageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService usersService;
    private final VillageService villageService;

    public UsersController(UsersService usersService, VillageService villageService) {
        this.usersService = usersService;
        this.villageService = villageService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserEntity> registerUser(@RequestBody UserRegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.registerUser(request));
    }

    @GetMapping("/users/{userId}/villages")
    public ResponseEntity<List<String>> getAllVillagesIdByUserId(@PathVariable String userId){
        return ResponseEntity.status(HttpStatus.OK).body(villageService.getAllVillagesIdByUserId(userId));
    }
}
