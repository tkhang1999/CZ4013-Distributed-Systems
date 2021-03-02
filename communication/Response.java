package communication;

import java.util.Objects;

public class Response<T> {
    public Header header;
    public T body;

    public Response() {
    }

    public Response(Header header, T body) {
        this.header = header;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        Response<?> response = (Response<?>) o;
        return Objects.equals(header, response.header) &&
        Objects.equals(body, response.body);
    }
}
