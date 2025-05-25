package org.turron.upload.mapper;

import org.mapstruct.Mapper;
import org.turron.upload.dto.VideoDto;
import org.turron.upload.entity.VideoEntity;

@Mapper(
        componentModel = "spring"
)
public interface VideoMapper {
    VideoDto toDto(VideoEntity entity);
}