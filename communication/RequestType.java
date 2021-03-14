package communication;

/**
 * The {@code RequestType} enum for type of a request
 */
public enum RequestType {
    TEST("test");

    public final String type;

    private RequestType(String type) {
        this.type = type;
    }

    // Get RequestType enum from its string value
    public static RequestType fromString(String text) {
        for (RequestType rt : RequestType.values()) {
            if (rt.type.equalsIgnoreCase(text)) {
                return rt;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return type;
    }
}
