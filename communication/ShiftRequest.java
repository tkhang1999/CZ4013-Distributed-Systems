package communication;

public class ShiftRequest extends Request {
    public String bookingId;
    public int postpone;
    public int period;

    public ShiftRequest() {};

    public ShiftRequest(int id, String bookingId, int postpone, int period) {
       super(id, RequestType.SHIFT.type);
        this.bookingId = bookingId;
        this.postpone = postpone;
        this.period = period;
    }
}
