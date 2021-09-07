package in.hp.kafka.libraryeventsproducer.controller;

import in.hp.kafka.libraryeventsproducer.config.KafkaPublishMode;
import in.hp.kafka.libraryeventsproducer.entity.LibraryEvent;
import in.hp.kafka.libraryeventsproducer.entity.LibraryEventType;
import in.hp.kafka.libraryeventsproducer.producer.LibraryEventPublisher;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Log4j2
@RestController
@RequestMapping("/library-events")
public class LibraryEventController {

    @Autowired
    private LibraryEventPublisher libraryEventPublisher;

    @PostMapping("/publish/{mode}")
    public ResponseEntity<LibraryEvent> createBook(
            @PathVariable KafkaPublishMode mode,
            @RequestBody LibraryEvent libraryEvent) {

        log.info("Received data to publish.");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventPublisher.sendLibraryEvent(mode, libraryEvent);
        log.info("Data published.");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("/publish/{mode}")
    public ResponseEntity<?> updateBook(
            @PathVariable KafkaPublishMode mode,
            @RequestBody LibraryEvent libraryEvent) {

        if (Objects.isNull(libraryEvent.getLibraryEventId())) {
            return ResponseEntity.badRequest().body("Please provide library event id");
        }

        log.info("Received data to publish.");
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventPublisher.sendLibraryEvent(mode, libraryEvent);
        log.info("Data published.");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
