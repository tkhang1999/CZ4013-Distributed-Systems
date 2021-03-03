package communication;

import java.util.Objects;
import java.util.UUID;

/**
 * Header of a request/response: a unique ID and a message
 */
public class Header {
    public UUID uuid;
    public String message;

    public Header() {}

    public Header(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Header)) return false;
        Header header = (Header) o;
        return Objects.equals(uuid, header.uuid) &&
        Objects.equals(message, header.message);
    }

    @Override
    public String toString() {
        return "RequestHeader(" + uuid + ", " + message + ")";
    }
}
