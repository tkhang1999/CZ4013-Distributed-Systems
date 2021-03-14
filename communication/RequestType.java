package communication;

public enum RequestType {
    TEST("test");

    public final String type;

    private RequestType(String type) {
        this.type = type;
    }

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
