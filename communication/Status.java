package communication;

public enum Status {
    OK("Ok"),
    DUPLICATE("Duplicate request"),
    NOT_FOUND("Service not found"),
    INTERNAL_ERR("Internal server error");

    public final String label;

    private Status(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
      	return label;
    }
}

