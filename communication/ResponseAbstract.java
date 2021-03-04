package communication;

public abstract class ResponseAbstract {

    public int id;
    public String status;

    public ResponseAbstract() {}

    public ResponseAbstract(int id, String status) {
        this.id = id;
        this.status = status;
    }
}
