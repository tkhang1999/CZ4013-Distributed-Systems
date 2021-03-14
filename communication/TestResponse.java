package communication;

public class TestResponse extends Response{
    public String content;

    public TestResponse() {}

    public TestResponse(Integer id, String status, String content) {
        super(id, status);
        this.content = content;
    }
}
