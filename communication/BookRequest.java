package communication;

public class BookRequest extends Request {
    public String facility;
    public int day;
    public String time;

    public BookRequest() {};

    public BookRequest(int id, String facility, int day, String time) {
        super(id, RequestType.BOOK.type);
        this.facility = facility;
        this.day = day;
        this.time = time;
    }
}
