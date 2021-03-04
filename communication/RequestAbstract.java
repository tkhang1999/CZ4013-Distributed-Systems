package communication;

public abstract class RequestAbstract {
    
    public int id;
    public String method;

    public RequestAbstract() {}

    public RequestAbstract(int id, String method) {
        this.id = id;
        this.method = method;
    }
}
