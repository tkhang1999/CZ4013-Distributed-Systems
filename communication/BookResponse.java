package communication;

public class BookResponse extends Response {
    public String content;

    public BookResponse() {};

    public BookResponse(int id, String status, String content) {
        super(id, status);
        this.content = content;
    }
}
