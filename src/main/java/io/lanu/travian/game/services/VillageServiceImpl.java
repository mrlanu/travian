package io.lanu.travian.game.services;

import io.lanu.travian.enums.VillageType;
import io.lanu.travian.game.entities.VillageEntity;
import io.lanu.travian.game.models.requests.NewVillageRequest;
import io.lanu.travian.game.repositories.VillageRepository;
import io.lanu.travian.templates.entities.VillageTemplate;
import io.lanu.travian.templates.repositories.VillageTemplatesRepo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class VillageServiceImpl implements VillageService{
    private final VillageRepository villageRepository;
    private final VillageTemplatesRepo villageTemplatesRepo;
    private final VillageGeneratorFacade facade;
    private final ModelMapper modelMapper;

    public VillageServiceImpl(VillageRepository villageRepository, VillageTemplatesRepo villageTemplatesRepo, VillageGeneratorFacade facade, ModelMapper modelMapper) {
        this.villageRepository = villageRepository;
        this.villageTemplatesRepo = villageTemplatesRepo;
        this.facade = facade;
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
        return villageRepository.save(newVillage);
    }

    @Override
    public VillageEntity getVillageById(String villageId) {
        VillageEntity village = villageRepository.findById(villageId).get();
        facade.generateVillage(village);
        return villageRepository.save(village);
    }

}
