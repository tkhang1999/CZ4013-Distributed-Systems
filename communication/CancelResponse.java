package communication;

public class CancelResponse extends Response {
    public String content; 

    public CancelResponse() {};

    public CancelResponse(int id, String status, String content) {
        super(id, status);
        this.content = content;
    }
}