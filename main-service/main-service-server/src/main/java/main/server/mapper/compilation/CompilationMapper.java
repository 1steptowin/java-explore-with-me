package main.server.mapper.compilation;

import main.server.dto.compilation.CompilationDto;
import main.server.dto.compilation.NewCompilationDto;
import main.server.mapper.event.EventMapper;
import main.server.model.compilation.Compilation;
import stats.client.StatsClient;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation mapDtoToModel(NewCompilationDto dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned() != null && dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public static CompilationDto mapModelToDto(Compilation model, StatsClient client) {
        return CompilationDto.builder()
                .id(model.getCompilationId())
                .title(model.getTitle())
                .pinned(model.getPinned())
                .events(model.getEvents().stream()
                        .map(event -> EventMapper.mapModelToShortDto(event, client))
                        .collect(Collectors.toList()))
                .build();
    }
}
