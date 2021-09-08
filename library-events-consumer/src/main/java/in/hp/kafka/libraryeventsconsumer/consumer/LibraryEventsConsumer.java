package in.hp.kafka.libraryeventsconsumer.consumer;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class LibraryEventsConsumer {

    @Value("{kafka.topic-id}")
    private String topicId;

    @KafkaListener(topics = {"library-events"})
    public void consumerMessage(ConsumerRecord<Integer, String> message) {
        log.info("Message Consumed:: {}", message);
    }
}
