package ewm.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import stats.client.StatsClient;

@Configuration
public class AppConfig {
    @Bean
    public StatsClient getStatsClient() {
        return new StatsClient();
    }
}
