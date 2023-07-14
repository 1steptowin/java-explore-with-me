package stats.server.controller;

import stats.dto.StatsRequestDto;
import stats.dto.StatsResponseDto;
import stats.server.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class StatsServerController {
    private final StatsService statsService;

    @Autowired
    public StatsServerController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping(value = "/hit")
    public ResponseEntity<Void> addHit(@RequestBody StatsRequestDto request, HttpServletRequest meta) {
        statsService.saveRecord(request, meta);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsResponseDto>> getStats(@RequestParam("start") String statsPeriodStart,
                                                           @RequestParam("end") String statsPeriodEnd,
                                                           @RequestParam(value = "uris", required = false) String[] uris,
                                                           @RequestParam(value = "unique", required = false) String unique) {
        return ResponseEntity.ok().body(statsService.getStats(statsPeriodStart, statsPeriodEnd, uris, unique));
    }
}
