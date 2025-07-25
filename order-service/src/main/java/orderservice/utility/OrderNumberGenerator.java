package orderservice.utility;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class OrderNumberGenerator {
    private static final String PREFIX = "WW";
    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis() % 100000);

    public String generate() {
        long timestamp = System.currentTimeMillis();
        long count = counter.incrementAndGet();
        return String.format("%s%d%04d", PREFIX, timestamp, count % 10000);
    }
}
