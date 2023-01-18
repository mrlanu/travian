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
    @GetMapping("/users/fake")
    public void createFakeUsers(){
        usersService.registerUser(new AuthRequest("mrlanu@gmail.com", "mrlanu", "12345"));
        usersService.registerUser(new AuthRequest("wer@yahoo.com", "wer", "12345"));
        usersService.registerUser(new AuthRequest("mrlan@gmail.com", "mrlan", "12345"));
        usersService.registerUser(new AuthRequest("wertu@yahoo.com", "wertu", "12345"));
        usersService.registerUser(new AuthRequest("mr@gmail.com", "mr", "12345"));
        usersService.registerUser(new AuthRequest("mama@yahoo.com", "mama", "12345"));
        usersService.registerUser(new AuthRequest("bob@gmail.com", "bob", "12345"));
        usersService.registerUser(new AuthRequest("igor@yahoo.com", "igor", "12345"));
        usersService.registerUser(new AuthRequest("nata@yahoo.com", "nata", "12345"));
        usersService.registerUser(new AuthRequest("ira@yahoo.com", "ira", "12345"));
        usersService.registerUser(new AuthRequest("sasha@yahoo.com", "sasha", "12345"));
        usersService.registerUser(new AuthRequest("sofka@yahoo.com", "sofka", "12345"));
    }

}
