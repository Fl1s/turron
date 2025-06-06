package org.turron.service.mapper;

import org.mapstruct.Mapper;
import org.turron.service.dto.SnippetDto;
import org.turron.service.entity.SnippetEntity;

@Mapper(
        componentModel = "spring"
)
public interface SnippetMapper {
    SnippetDto toDto(SnippetEntity entity);
}