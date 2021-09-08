package in.hp.kafka.libraryeventsconsumer.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Book {

    @Id
    private Integer bookId;

    private String name;

    private String author;

    // excluding to avoid circular dependency and stackoverflow error
    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "libraryEventId")
    private LibraryEvent libraryEvent;
}
