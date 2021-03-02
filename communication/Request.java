package communication;

import java.util.Objects;

public class Request<T> {
    public Header header;
    public T body;

    public Request() {}

    public Request(Header header, T body) {
        this.header = header;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request<?> request = (Request<?>) o;
        return Objects.equals(header, request.header) &&
        Objects.equals(body, request.body);
    }
}
