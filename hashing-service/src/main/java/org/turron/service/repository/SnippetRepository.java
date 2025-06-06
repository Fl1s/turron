package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.SnippetEntity;

@Repository
public interface SnippetRepository extends JpaRepository<SnippetEntity, String> {
}
