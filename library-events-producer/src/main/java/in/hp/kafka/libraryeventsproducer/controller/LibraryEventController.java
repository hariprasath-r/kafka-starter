package in.hp.kafka.libraryeventsproducer.controller;

import in.hp.kafka.libraryeventsproducer.entity.LibraryEvent;
import in.hp.kafka.libraryeventsproducer.producer.LibraryEventProducer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
@RequestMapping("/library-events")
public class LibraryEventController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;

    @PostMapping("/async")
    public ResponseEntity<LibraryEvent> createBook(@RequestBody LibraryEvent libraryEvent) {
        log.info("Received data to publish.");
        CompletableFuture.runAsync(() -> libraryEventProducer.sendEventAsync(libraryEvent));
        log.info("Data published.");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/sync")
    public ResponseEntity<LibraryEvent> createBookSync(@RequestBody LibraryEvent libraryEvent) {
        log.info("Received data to publish.");
        libraryEventProducer.sendEventSync(libraryEvent);
        log.info("Data published.");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
