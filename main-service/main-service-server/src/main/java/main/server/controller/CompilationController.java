package main.server.controller;

import main.server.dto.compilation.CompilationDto;
import main.server.dto.compilation.NewCompilationDto;
import main.server.dto.compilation.UpdateCompilationRequest;
import main.server.service.compilation.CompilationService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationController {
    CompilationService compilationService;

    @Autowired
    public CompilationController(CompilationService compilationService) {
        this.compilationService = compilationService;
    }

    @PostMapping(value = PathsConstants.COMPILATION_ADMIN_PATH)
    public ResponseEntity<CompilationDto> addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.addCompilation(newCompilationDto));
    }

    @PatchMapping(value = PathsConstants.COMPILATION_ADMIN_BY_ID_PATH)
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable("compId") Long compId,
                                                            @Valid @RequestBody UpdateCompilationRequest request) {
        return ResponseEntity.ok().body(compilationService.updateCompilation(compId, request));
    }

    @GetMapping(PathsConstants.COMPILATION_PUBLIC_BY_ID_PATH)
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compId") Long compId) {
        return ResponseEntity.ok().body(compilationService.getCompilationById(compId));
    }

    @GetMapping(PathsConstants.COMPILATION_PUBLIC_PATH)
    public ResponseEntity<List<CompilationDto>> getAllCompilations(@RequestParam(name = "pinned", required = false) Optional<Boolean> pinned,
                                                                   @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                                   @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok().body(compilationService.getAllCompilations(pinned, from, size));
    }

    @DeleteMapping(PathsConstants.COMPILATION_ADMIN_BY_ID_PATH)
    public ResponseEntity<Void> deleteCompilation(@PathVariable("compId") Long compId) {
        compilationService.deleteCompilation(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
