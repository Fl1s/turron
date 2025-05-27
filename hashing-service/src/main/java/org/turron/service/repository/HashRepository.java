package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.HashEntity;

@Repository
public interface HashRepository extends JpaRepository<HashEntity, String> {
}
