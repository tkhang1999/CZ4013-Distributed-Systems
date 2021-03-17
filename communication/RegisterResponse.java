package communication;

public class RegisterResponse extends Response {
    public String content; 
    public int interval;

    public RegisterResponse() {};

    public RegisterResponse(int id, String status, String content, int interval) {
        super(id, status);
        this.content = content;
        this.interval = interval;
    }
}
