package in.hp.kafka.libraryeventsproducer.config;

import java.util.Arrays;

public enum KafkaPublishMode {
    SYNC("sync"),
    ASYNC("async");

    private final String publishMode;

    KafkaPublishMode(String publishMode) {
        this.publishMode = publishMode;
    }

    public static KafkaPublishMode getPublishMode(String mode) {
        return Arrays.stream(KafkaPublishMode.values())
                .filter(kafkaPublishMode -> kafkaPublishMode.publishMode.equals(mode))
                .findFirst()
                .orElse(null);
    }

    public String getPublishMode() {
        return publishMode;
    }
}
