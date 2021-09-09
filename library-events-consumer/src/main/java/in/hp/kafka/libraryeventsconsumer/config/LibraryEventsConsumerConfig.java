package in.hp.kafka.libraryeventsconsumer.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

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
        factory.setErrorHandler(((thrownException, data)
                -> log.info("Exception thrown {} while processing data: {}", thrownException, data)));

        // adding retries
        factory.setRetryTemplate(getRetryTemplate());

        return factory;
    }

    /**
     * Configuring retry template
     *  1. retry policy
     *  2. back off policy
     */
    private RetryTemplate getRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(getRetryPolicy());
        retryTemplate.setBackOffPolicy(getBackOffPolicy());
        return retryTemplate;
    }

    /**
     * Configured retry policy with the below properties
     *  1. number of attempts to retry
     *  2. what all exceptions to retry
     */
    private RetryPolicy getRetryPolicy() {
        final Map<Class<? extends Throwable>, Boolean> retryableExceptionsMap = Map.of(
                IllegalArgumentException.class, false,
                RecoverableDataAccessException.class, true
        );
        return new SimpleRetryPolicy(3, retryableExceptionsMap, true);
    }

    /**
     * Configures back off policy
     *  1. How long to wait before attempting to retry
     */
    private BackOffPolicy getBackOffPolicy() {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(1000);
        return fixedBackOffPolicy;
    }
}
