package in.hp.kafka.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.hp.kafka.libraryeventsproducer.config.KafkaPublishMode;
import in.hp.kafka.libraryeventsproducer.entity.Book;
import in.hp.kafka.libraryeventsproducer.entity.LibraryEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log4j2
@Configuration
public class LibraryEventPublisher {

    private final String TOPIC = "library-events";

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendLibraryEvent(KafkaPublishMode publishMode, LibraryEvent libraryEvent) {
        log.info("Kafka Publish Mode: {}", publishMode);
        if (publishMode.equals(KafkaPublishMode.SYNC)) {
            sendEventSync(libraryEvent);
        } else {
            sendEventAsyncV1(libraryEvent);
        }
    }

    /**
     * Synchronous approach. Since we are using get() in a Future the thread will wait to complete
     */
    @SuppressWarnings("java:S2142")
    public void sendEventSync(LibraryEvent libraryEvent) {
        var key = libraryEvent.getEventId();
        var value = libraryEvent.getBook();
        log.info("Sending key: {}, value: {}", key, value);
        try {
            SendResult<Integer, String> sendResult =
                    kafkaTemplate.sendDefault(key, toJson(value)).get(1, TimeUnit.SECONDS);
            log.info("Send Result: {}", sendResult);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            log.error("Exception in sending message. Message: {}", e.getMessage());
        }
    }

    /**
     * Complete Async Approach
     */
    public void sendEventAsync(LibraryEvent libraryEvent) {
        var key = libraryEvent.getEventId();
        var value = libraryEvent.getBook();
        log.info("Sending key: {}, value: {}", key, value);
        kafkaTemplate
                .sendDefault(key, toJson(value))
                .addCallback(new SendResultCustomCallback());
    }

    public void sendEventAsyncV1(LibraryEvent libraryEvent) {
        var key = libraryEvent.getEventId();
        var value = libraryEvent.getBook();
        log.info("Sending key: {}, value: {}", key, value);
        kafkaTemplate
                .send(buildProducerRecord(key, value))
                .addCallback(new SendResultCustomCallback());
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, Book value) {
        Iterable<Header> headers = List.of(new RecordHeader("event-source", "test-event-source".getBytes()));
        return new ProducerRecord<>(TOPIC, null, key, toJson(value), headers);
    }

    private String toJson(Book book) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(book);
        } catch (JsonProcessingException ex) {
            log.error("Exception in converting to JSON String: {}", ex.getMessage());
        }
        return json;
    }
}
