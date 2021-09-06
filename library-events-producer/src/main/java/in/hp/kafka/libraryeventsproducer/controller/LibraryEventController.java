package in.hp.kafka.libraryeventsproducer.controller;

import in.hp.kafka.libraryeventsproducer.entity.LibraryEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/library-events")
public class LibraryEventController {

    @PostMapping
    public ResponseEntity<LibraryEvent> createBook(@RequestBody LibraryEvent libraryEvent) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }
}
