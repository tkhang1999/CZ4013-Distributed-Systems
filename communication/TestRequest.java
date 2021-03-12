package communication;

public class TestRequest extends Request {
    public float[] content;

    public TestRequest() {}

    public TestRequest(Integer id, String method, float[] content) {
        super(id, method);
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestRequest)) return false;
        TestRequest request = (TestRequest) o;
        return id == request.id && method.equals(request.method) 
            && content == request.content;
    }
}
