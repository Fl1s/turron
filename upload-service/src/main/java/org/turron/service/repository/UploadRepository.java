package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.VideoEntity;

@Repository
public interface UploadRepository extends JpaRepository<VideoEntity, String> {
}
