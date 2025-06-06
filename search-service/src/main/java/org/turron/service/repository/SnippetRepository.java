package org.turron.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.turron.service.entity.SnippetEntity;

import java.util.List;

@Repository
public interface SnippetRepository extends JpaRepository<SnippetEntity, String> {
    @Query("SELECT f.frameHash FROM SnippetEntity f WHERE f.snippetId = :snippetId")
    List<String> findHashesBySnippetId(@Param("snippetId") String videoId);
}
