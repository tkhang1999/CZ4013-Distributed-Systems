package communication;

public class Response {
    public Integer id;
    public String status;
    public String content;

    public Response() {}

    public Response(Integer id, String status, String content) {
        this.id = id;
        this.status = status;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        Response response = (Response) o;
        return id == response.id && status.equals(response.status) 
            && content.equals(response.content);
    }
}
