package communication;

public class RegisterRequest extends Request {
    public String facility;
    public int interval;

    public RegisterRequest() {};

    public RegisterRequest(int id, String facility, int interval) {
        super(id, RequestType.REGISTER.type);
        this.facility = facility;
        this.interval = interval;
    }
}
