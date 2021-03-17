package communication;

/**
 * The {@code Status} enum for status of a response
 */
public enum Status {
    OK("Ok"),
    DUPLICATE("Duplicate request"),
    NOT_FOUND("Service not found"),
    INVALID("Invalid request"),
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

