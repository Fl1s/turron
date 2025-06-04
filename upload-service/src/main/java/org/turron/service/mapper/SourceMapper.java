package org.turron.service.mapper;

import org.mapstruct.Mapper;
import org.turron.service.dto.SourceDto;
import org.turron.service.dto.VideoDto;
import org.turron.service.entity.SourceEntity;
import org.turron.service.entity.VideoEntity;

@Mapper(
        componentModel = "spring"
)
public interface SourceMapper {
    SourceDto toDto(SourceEntity entity);
}