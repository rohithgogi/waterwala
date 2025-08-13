package businessservice.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeFlexibleDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // If it's an object with hour/minute fields
        if (node.isObject() && node.has("hour") && node.has("minute")) {
            int hour = node.get("hour").asInt();
            int minute = node.get("minute").asInt();
            return LocalTime.of(hour, minute);
        }

        // If it's a text node (string like "09:00" or "09:00:00")
        if (node.isTextual()) {
            String timeString = node.asText();
            return LocalTime.parse(timeString, DateTimeFormatter.ofPattern("H:mm[:ss]"));
        }

        return null;
    }
}
