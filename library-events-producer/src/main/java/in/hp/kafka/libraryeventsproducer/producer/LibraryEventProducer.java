package in.hp.kafka.libraryeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.hp.kafka.libraryeventsproducer.entity.Book;
import in.hp.kafka.libraryeventsproducer.entity.LibraryEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Log4j2
@Configuration
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendEvent(LibraryEvent libraryEvent) {
        var key = libraryEvent.getEventId();
        var value = libraryEvent.getBook();
        log.info("Sending key: {}, value: {}", key, value);
        kafkaTemplate
                .sendDefault(key, toJson(value))
                .addCallback(new SendResultCustomCallback());
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
