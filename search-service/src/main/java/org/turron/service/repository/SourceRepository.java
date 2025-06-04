package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.SourceEntity;

import java.util.List;
@Repository
public interface SourceRepository extends JpaRepository<SourceEntity, Long> {
    @Query("SELECT DISTINCT f.sourceId FROM SourceEntity f")
    List<String> findDistinctSourceIds();
    @Query("SELECT f.frameHash FROM SourceEntity f WHERE f.sourceId = :sourceId")
    List<String> findHashesBySourceId(@Param("sourceId") String sourceId);
}
