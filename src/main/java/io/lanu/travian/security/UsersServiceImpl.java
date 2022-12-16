package io.lanu.travian.security;

import io.lanu.travian.enums.SettlementType;
import io.lanu.travian.errors.UserErrorException;
import io.lanu.travian.game.entities.StatisticsEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.repositories.StatisticsRepository;
import io.lanu.travian.game.services.SettlementService;
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
    private final SettlementService settlementService;
    private final StatisticsRepository statisticsRepository;


    public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SettlementService settlementService, StatisticsRepository statisticsRepository) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.settlementService = settlementService;
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public UserEntity registerUser(AuthRequest request) {
        if (usersRepository.findByEmail(request.getEmail()).isPresent()){
            throw new UserErrorException(String.format("Email %s already exists", request.getEmail()));
        }
        request.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        var statistics = new StatisticsEntity(request.getUsername(), null, 2, 1,
                "", 0, 0);
        var statisticsEntity = statisticsRepository.save(statistics);
        var user = usersRepository
                .save(new UserEntity(null, request.getEmail(), request.getUsername(), statisticsEntity.getId(),
                        request.getPassword()));
        statisticsEntity.setPlayerId(user.getUserId());
        statisticsRepository.save(statisticsEntity);
        var villageRequest = new NewVillageRequest(user.getUserId(), user.getUsername(), SettlementType.VILLAGE,
                0, 0);
        settlementService.newVillage(villageRequest);
        return user;
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
