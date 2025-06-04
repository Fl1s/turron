package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.VideoEntity;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, String> {
    @Query("SELECT f.frameHash FROM VideoEntity f WHERE f.videoId = :videoId")
    List<String> findHashesByVideoId(@Param("videoId") String videoId);
}
