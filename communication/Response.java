package communication;

public abstract class Response {

    public int id;
    public String status;

    public Response() {}

    public Response(int id, String status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        Response response = (Response) o;
        return id == response.id;
    }
}
