package in.hp.kafka.libraryeventsproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Profile("local")
@Configuration
public class AutoCreateConfig {

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder
                .name("library-events")
                .replicas(2)
                .partitions(2)
                .build();

    }
}
