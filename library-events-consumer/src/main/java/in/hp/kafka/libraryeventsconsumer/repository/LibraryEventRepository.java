package in.hp.kafka.libraryeventsconsumer.repository;

import in.hp.kafka.libraryeventsconsumer.entity.LibraryEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibraryEventRepository extends CrudRepository<LibraryEvent, Integer> {
}
