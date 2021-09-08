package in.hp.kafka.libraryeventsconsumer.service;

import in.hp.kafka.libraryeventsconsumer.entity.LibraryEvent;
import in.hp.kafka.libraryeventsconsumer.repository.LibraryEventRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class LibraryEventService {

    @Autowired
    private LibraryEventRepository libraryEventRepository;

    public void addLibraryEvent(LibraryEvent libraryEvent) {
       log.info("Adding new library event: {}", libraryEvent);
       libraryEvent.getBook().setLibraryEvent(libraryEvent);
       libraryEventRepository.save(libraryEvent);
       log.info("Added new library event.");
    }
}
