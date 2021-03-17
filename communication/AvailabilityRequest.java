package communication;

public class AvailabilityRequest extends Request {
    public String facility;
    public int[] days;

    public AvailabilityRequest() {};

    public AvailabilityRequest(int id, String facility, int[] days) {
        super(id, RequestType.AVAILABILITY.type);
        this.facility = facility;
        this.days = days;
    }
}
