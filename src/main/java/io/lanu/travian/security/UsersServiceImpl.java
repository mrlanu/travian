package io.lanu.travian.security;

import io.lanu.travian.enums.EVillageType;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.services.VillageService;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment environment;
    private final VillageService villageService;


    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, Environment environment, VillageService villageService) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.environment = environment;
        this.villageService = villageService;
    }

    @Override
    public UserEntity registerUser(UserRegisterRequest request) {
        request.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        var user = usersRepository
                .save(new UserEntity(null, request.getEmail(), request.getUsername(), request.getPassword()));
        var villageRequest = new NewVillageRequest(user.getUserId(), EVillageType.SIX,
                getRandomCoordinate(-100, 100), getRandomCoordinate(-100, 100));
        villageService.createVillage(villageRequest);
        return user;
    }

    private int getRandomCoordinate(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with username - %s is not exist.", email))
        );
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email - %s is not exist.", email))
        );
    }
}
