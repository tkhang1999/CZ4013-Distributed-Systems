package communication;

public class ExtendRequest extends Request {
    public String bookingId;
    public int sooner;
    public int period;

    public ExtendRequest() {};

    public ExtendRequest(int id, String bookingId, int sooner, int period) {
       super(id, RequestType.EXTEND.type);
       this.bookingId = bookingId;
       this.sooner = sooner;
       this.period = period;
    }
}
