package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {
    /*List<ReportEntity> findAllByAttackerIdOrDefenderId(String attackerId, String defenderId);*/
    List<ReportEntity> findAllByReportOwner(String settlementId);
}
