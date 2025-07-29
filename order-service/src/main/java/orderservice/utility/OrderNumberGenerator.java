package orderservice.utility;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderNumberGenerator {
    private static final String PREFIX = "WW";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private final AtomicLong counter = new AtomicLong(1);

    public synchronized String generate() {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        long count = counter.getAndIncrement();

        // Reset counter if it gets too large (prevent overflow)
        if (count > 999999) {
            counter.set(1);
            count = 1;
        }

        return String.format("%s%s%06d", PREFIX, timestamp, count);
    }

    // For testing purposes
    public void resetCounter() {
        counter.set(1);
    }
}