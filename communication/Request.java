package communication;

public class Request implements RequestInterface {
    public Integer id;
    public String method;
    public float[] content;

    public Request() {}

    public Request(Integer id, String method, float[] content) {
        this.id = id;
        this.method = method;
        this.content = content;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getMethod() {
        return method;
    }

    public float[] getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return id == request.id && method.equals(request.method) 
            && content == request.content;
    }
}
