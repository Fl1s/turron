package org.turron.memory.mapper;

import org.mapstruct.Mapper;
import org.turron.memory.dto.ThoughtDto;
import org.turron.memory.entity.ThoughtEntity;
import org.turron.memory.event.ThoughtEvent;

@Mapper(
        componentModel = "spring"
)
public interface ThoughtMapper {
    ThoughtDto toDto(ThoughtEntity thought);
    ThoughtEntity toEntity(ThoughtEvent dto);
}
