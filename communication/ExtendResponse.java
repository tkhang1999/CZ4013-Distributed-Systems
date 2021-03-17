package communication;

public class ExtendResponse extends Response{
    public String content;

    public ExtendResponse() {}

    public ExtendResponse(int id, String status, String content) {
        super(id, status);
        this.content = content;
    }
}
