package in.hp.kafka.libraryeventsproducer.producer;

import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Objects;
import java.util.Optional;

@Log4j2
public class SendResultCustomCallback implements ListenableFutureCallback<SendResult<Integer, String>> {

    @Override
    public void onFailure(Throwable ex) {
        log.error("Error in sending message to kafka.");
        log.error("Exception message: {}", ex.getMessage());
    }

    @Override
    public void onSuccess(SendResult<Integer, String> result) {
        if (Objects.nonNull(result)) {
            var partition = result.getRecordMetadata().partition();
            Optional.ofNullable(result.getProducerRecord()).ifPresentOrElse(producerRecord -> {
                var key = producerRecord.key();
                var value = producerRecord.value();
                log.info("Message Sent Successfully. Key {}, Value {}, Partition {}", key, value, partition);
            }, () -> {
                log.info("Message Sent Successfully to Partition {}.", partition);
            });
        } else {
            log.info("Message Sent Successfully.");
        }
    }
}
