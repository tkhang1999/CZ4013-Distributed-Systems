package communication;

public class Request extends RequestAbstract {
    public float[] content;

    public Request() {}

    public Request(Integer id, String method, float[] content) {
        super(id, method);
        this.content = content;
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
