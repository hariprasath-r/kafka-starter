package in.hp.kafka.libraryeventsconsumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.hp.kafka.libraryeventsconsumer.entity.LibraryEvent;
import in.hp.kafka.libraryeventsconsumer.service.LibraryEventService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class LibraryEventsConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LibraryEventService libraryEventService;

    @KafkaListener(topics = {"library-events"})
    public void consumerMessage(ConsumerRecord<Integer, String> message) {
        log.info("Message Consumed:: {}", message);
        processLibraryEvent(message);
    }

    private void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) {
        try {
            var libraryEvent = retrieveLibraryEvent(consumerRecord.value());
            switch (libraryEvent.getLibraryEventType()) {
                case NEW:
                    libraryEventService.addLibraryEvent(libraryEvent);
                    break;
                case UPDATE:
                    break;
                default:
                    log.info("Default case");
            }
        } catch (JsonProcessingException ex) {
            log.error("Error Parsing Library Event from Consumer Record");
        }
    }

    private LibraryEvent retrieveLibraryEvent(String data) throws JsonProcessingException {
        return objectMapper.readValue(data, LibraryEvent.class);
    }
}
