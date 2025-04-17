package org.turron.ingestion.mapper;

import org.mapstruct.Mapper;
import org.turron.ingestion.dto.ThoughtDto;
import org.turron.ingestion.event.ThoughtEvent;

@Mapper(
        componentModel = "spring"
)
public interface ThoughtEventMapper {
    ThoughtEvent toEventFromDto(ThoughtDto thought);
}
