package stats.server.repo;

import stats.server.model.StatsRecord;
import stats.dto.StatsResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepo extends JpaRepository<StatsRecord, Long> {
    @Query("SELECT new stats.dto.StatsResponseDto(r.app, r.uri, COUNT(r.uri)) " +
            "FROM StatsRecord as r " +
            "WHERE r.timestamp BETWEEN :start AND :end " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(r.uri) DESC")
    List<StatsResponseDto> getStatsForDates(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    @Query("SELECT new stats.dto.StatsResponseDto(r.app, r.uri, COUNT(r.uri)) " +
            "FROM StatsRecord as r " +
            "WHERE r.timestamp BETWEEN :start AND :end " +
            "AND r.uri IN :uris " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(r.uri) DESC")
    List<StatsResponseDto> getStatsForDatesAndUris(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("uris") String[] uris);

    @Query("SELECT new stats.dto.StatsResponseDto(r.app, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM StatsRecord as r " +
            "WHERE r.timestamp BETWEEN :start AND :end " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC")
    List<StatsResponseDto> getStatsForDatesWithUniqueIp(@Param("start") LocalDateTime start,
                                                        @Param("end") LocalDateTime end);

    @Query("SELECT new stats.dto.StatsResponseDto(r.app, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM StatsRecord as r " +
            "WHERE r.timestamp BETWEEN :start AND :end " +
            "AND r.uri IN :uris " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC")
    List<StatsResponseDto> getStatsForDatesAndUrisWithUniqueIp(@Param("start") LocalDateTime start,
                                                               @Param("end") LocalDateTime end,
                                                               @Param("uris") String[] uris);
}