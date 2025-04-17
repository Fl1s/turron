package org.turron.thought.mapper;

import org.mapstruct.Mapper;
import org.turron.thought.dto.ThoughtDto;
import org.turron.thought.event.ThoughtEvent;

@Mapper(
        componentModel = "spring"
)
public interface ThoughtEventMapper {
    ThoughtEvent toEventFromDto(ThoughtDto thought);
}
