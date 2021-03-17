package communication;

public final class Constants {
    public static final String SERVER_ADDRESS = "127.0.0.1";
    public static final int SERVER_PORT = 2222;
    public static final int MAX_TRIES = 1;
    public static final int TIME_OUT = 1000;
    public static final double FAIL_PROBABILITY = 0.1;
    public static final int BUFFER_LENGTH = 512;
    public static final InvocationMethod INVOCATION_METHOD = InvocationMethod.AT_MOST_ONCE;

    public static final int SAMPLE_SERVICE = -1;
    public static final int AVAILABILITY_SERVICE = 1;
    public static final int BOOK_SERVICE = 2;
    public static final int SHIFT_SERVICE = 3;
    public static final int REGISTER_SERVICE = 4;
    public static final int CANCEL_SERVICE = 5;
    public static final int EXTEND_SERVICE = 6;
    public static final int END_SERVICE = 0;
}
