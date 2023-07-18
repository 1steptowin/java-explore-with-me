package main.server.service.compilation;

import main.server.dto.compilation.CompilationDto;
import main.server.dto.compilation.NewCompilationDto;
import main.server.dto.compilation.UpdateCompilationRequest;
import main.server.exception.compilation.CompilationNotFoundException;
import main.server.mapper.compilation.CompilationMapper;
import main.server.model.compilation.Compilation;
import main.server.model.event.Event;
import main.server.repo.compilation.CompilationRepo;
import main.server.repo.event.EventRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.server.service.EWMPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stats.client.StatsClient;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationServiceImpl implements CompilationService {
    CompilationRepo compilationRepo;
    EventRepo eventRepo;
    StatsClient statsClient;

    @Transactional
    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation added = CompilationMapper.mapDtoToModel(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            List<Event> compiled = eventRepo.findAllById(newCompilationDto.getEvents());
            added.setEvents(new HashSet<>(compiled));
        } else {
            added.setEvents(new HashSet<>());
        }
        return CompilationMapper.mapModelToDto(compilationRepo.save(added), statsClient);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation update = compilationRepo.findById(compId).orElseThrow(() -> {
            throw new CompilationNotFoundException("Compilation does not exist");
        });
        if (request.getTitle() != null) {
            update.setTitle(request.getTitle());
        }
        if (request.getPinned() != null) {
            update.setPinned(request.getPinned());
        }
        if (request.getEvents() != null) {
            List<Event> eventsUpdated = eventRepo.findAllById(request.getEvents());
            update.setEvents(new HashSet<>(eventsUpdated));
        }
        return CompilationMapper.mapModelToDto(compilationRepo.save(update), statsClient);
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilationFound = compilationRepo.findById(compId).orElseThrow(() -> {
            throw new CompilationNotFoundException("Compilation does not exist");
        });
        return CompilationMapper.mapModelToDto(compilationFound, statsClient);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Optional<Boolean> pinned, int from, int size) {
        List<Compilation> compilations;
        if (pinned.isEmpty()) {
            compilations = compilationRepo.findAll(new EWMPageRequest(from, size)).getContent();
        } else {
            compilations = compilationRepo.findAllByPinned(pinned.get());
        }
        return compilations.stream().map(c -> CompilationMapper.mapModelToDto(c, statsClient))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        compilationRepo.deleteById(compId);
    }
}
