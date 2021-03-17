package communication;

public class ShiftResponse extends Response{
    public String content;

    public ShiftResponse() {}

    public ShiftResponse(int id, String status, String content) {
        super(id, status);
        this.content = content;
    }
}
