package communication;

public class AvailabilityResponse extends Response {
    public String content;

    public AvailabilityResponse() {};

    public AvailabilityResponse(int id, String status, String content) {
        super(id, status);
        this.content = content;
    }
}
