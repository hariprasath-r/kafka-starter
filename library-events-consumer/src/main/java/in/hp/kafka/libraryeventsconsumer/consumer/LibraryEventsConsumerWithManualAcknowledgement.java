package in.hp.kafka.libraryeventsconsumer.consumer;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

//@Component
@Log4j2
public class LibraryEventsConsumerWithManualAcknowledgement implements AcknowledgingMessageListener<Integer, String> {

    @Override
    @KafkaListener(topics = "library-events")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord, Acknowledgment acknowledgment) {
        log.info("Consumed Message:: {}", consumerRecord);
        log.info("Sending Acknowledgement.");
        acknowledgment.acknowledge();
    }
}
