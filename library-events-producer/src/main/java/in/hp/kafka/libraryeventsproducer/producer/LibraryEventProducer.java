package in.hp.kafka.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.hp.kafka.libraryeventsproducer.entity.Book;
import in.hp.kafka.libraryeventsproducer.entity.LibraryEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log4j2
@Configuration
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

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
