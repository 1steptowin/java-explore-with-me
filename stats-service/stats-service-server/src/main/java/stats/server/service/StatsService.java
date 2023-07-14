package stats.server.service;

import stats.dto.StatsRequestDto;
import stats.dto.StatsResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface StatsService {
    void saveRecord(StatsRequestDto request, HttpServletRequest meta);

    List<StatsResponseDto> getStats(String start, String end, String[] uris, String unique);
}
