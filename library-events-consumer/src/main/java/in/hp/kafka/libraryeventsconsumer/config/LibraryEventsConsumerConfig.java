package in.hp.kafka.libraryeventsconsumer.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Log4j2
@Configuration
@EnableKafka
public class LibraryEventsConsumerConfig {

    private KafkaProperties properties;

    public LibraryEventsConsumerConfig(KafkaProperties kafkaProperties) {
        this.properties = kafkaProperties;
    }

    @Bean
    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));

        // To enable manual acknowledgement mode
        // factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        // To enable concurrent consumers - parameter -> thread count
         factory.setConcurrency(3);

        // adding custom error handler
        factory.setErrorHandler(((thrownException, data) -> {
            log.info("Exception thrown {} while processing data: {}", thrownException, data);
        }));

        return factory;
    }
}
