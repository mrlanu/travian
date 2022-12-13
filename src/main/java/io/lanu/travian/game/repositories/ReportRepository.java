package io.lanu.travian.game.repositories;

import io.lanu.travian.game.entities.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepository extends MongoRepository<ReportEntity, String> {
    List<ReportEntity> findAllByReportOwner(String settlementId);
    long countAllByReportOwnerAndRead(String ownerId, boolean read);
}
