package communication;

public class CancelRequest extends Request{

    public String bookingId;

    public CancelRequest() {};

    public CancelRequest(int id, String bookingId) {
        super(id, RequestType.CANCEL.type);
        this.bookingId = bookingId;
    }
}
