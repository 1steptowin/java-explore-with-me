package main.server.service.compilation;

import main.server.dto.compilation.CompilationDto;
import main.server.dto.compilation.NewCompilationDto;
import main.server.dto.compilation.UpdateCompilationRequest;

import java.util.List;
import java.util.Optional;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getAllCompilations(Optional<Boolean> pinned, int from, int size);

    void deleteCompilation(Long compId);
}
