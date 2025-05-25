package org.turron.upload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.upload.entity.VideoEntity;

@Repository
public interface UploadRepository extends JpaRepository<VideoEntity, String> {
}
