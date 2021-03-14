package communication;

public abstract class Request {
    
    public int id;
    public String type;

    public Request() {}

    public Request(int id, String type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return id == request.id;
    }
}
