package org.turron.service.mapper;

import org.mapstruct.Mapper;
import org.turron.service.dto.SourceDto;
import org.turron.service.entity.SourceEntity;

@Mapper(
        componentModel = "spring"
)
public interface SourceMapper {
    SourceDto toDto(SourceEntity entity);
}