package communication;

public class TestRequest extends Request {
    public float[] content;

    public TestRequest() {}

    public TestRequest(Integer id, float[] content) {
        super(id, RequestType.TEST.type);
        this.content = content;
    }
}
