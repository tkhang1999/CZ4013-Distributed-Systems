package communication;

public class Request {
    
    public int id;
    public String method;

    public Request() {}

    public Request(int id, String method) {
        this.id = id;
        this.method = method;
    }
}
