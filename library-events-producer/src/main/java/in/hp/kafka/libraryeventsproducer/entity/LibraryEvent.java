package in.hp.kafka.libraryeventsproducer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LibraryEvent {
    private Integer eventId;
    private LibraryEventType libraryEventType;
    private Book book;
}
