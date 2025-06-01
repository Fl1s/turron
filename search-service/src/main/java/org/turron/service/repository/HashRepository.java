package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.HashEntity;

import java.util.List;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {
    @Query("SELECT DISTINCT f.videoId FROM HashEntity f")
    List<String> findDistinctVideoIds();

    @Query("SELECT f.frameHash FROM HashEntity f WHERE f.videoId = :videoId")
    List<String> findHashesByVideoId(@Param("videoId") String videoId);
}
