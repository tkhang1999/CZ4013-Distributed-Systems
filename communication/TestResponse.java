package communication;

public class TestResponse extends Response{
    public String content;

    public TestResponse() {}

    public TestResponse(Integer id, String status, String content) {
        super(id, status);
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestResponse)) return false;
        TestResponse response = (TestResponse) o;
        return id == response.id && status.equals(response.status) 
            && content.equals(response.content);
    }
}
