package org.turron.upload.mapper;

import org.mapstruct.Mapper;
import org.turron.upload.dto.VideoDto;
import org.turron.upload.entity.VideoEntity;
import org.turron.upload.event.VideoUploadedEvent;

@Mapper(
        componentModel = "spring"
)
public interface VideoMapper {
    VideoUploadedEvent toVideoUploadedEvent(VideoEntity videoEntity);
    VideoDto toDto(VideoEntity entity);
}