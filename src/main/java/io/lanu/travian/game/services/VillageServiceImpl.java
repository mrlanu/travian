package io.lanu.travian.game.services;

import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.repositories.VillageRepo;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepo villageRepo;
    private final VillageTemplatesRepo villageTemplatesRepo;
    private final ModelMapper modelMapper;

    public VillageServiceImpl(VillageRepo villageRepo, VillageTemplatesRepo villageTemplatesRepo, ModelMapper modelMapper) {
        this.villageRepo = villageRepo;
        this.villageTemplatesRepo = villageTemplatesRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public VillageEntity createVillage(NewVillageRequest newVillageRequest) {
        VillageTemplate villageTemplate = villageTemplatesRepo.findByVillageType(VillageType.SIX);
        villageTemplate.setAccountId(newVillageRequest.getAccountId());
        villageTemplate.setX(100);
        villageTemplate.setY(100);
        villageTemplate.setPopulation(100);
        villageTemplate.setCulture(0);
        VillageEntity newVillage = modelMapper.map(villageTemplate, VillageEntity.class);
        return villageRepo.save(newVillage);
    }

    @Override
    public VillageEntity getVillageById(String villageId) {
        VillageEntity village = villageRepo.findById(villageId).get();
        VillageGeneratorFacade facade = new VillageGeneratorFacade(village);
        facade.generateVillage();
        return villageRepo.save(village);
    }

}
