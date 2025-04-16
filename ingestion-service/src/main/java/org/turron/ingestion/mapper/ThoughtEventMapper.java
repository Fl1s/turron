package org.turron.ingestion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.turron.ingestion.dto.ThoughtDto;
import org.turron.ingestion.entity.ThoughtEntity;
import org.turron.ingestion.event.ThoughtEvent;

@Mapper(
        componentModel = "spring"
)
public interface ThoughtEventMapper {
    ThoughtEntity toEntity(ThoughtEvent thoughtEvent);

    ThoughtEvent toEventFromEntity(ThoughtEntity thought);
    ThoughtEvent toEventFromDto(ThoughtDto thought);

    void updateEntityFromEvent(ThoughtEvent thoughtEvent, @MappingTarget ThoughtEntity thought);

}
