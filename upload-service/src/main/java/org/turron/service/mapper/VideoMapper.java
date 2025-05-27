package org.turron.service.mapper;

import org.mapstruct.Mapper;
import org.turron.service.dto.VideoDto;
import org.turron.service.entity.VideoEntity;
import org.turron.service.event.VideoUploadedEvent;

@Mapper(
        componentModel = "spring"
)
public interface VideoMapper {
    VideoUploadedEvent toVideoUploadedEvent(VideoEntity videoEntity);

    VideoDto toDto(VideoEntity entity);
}