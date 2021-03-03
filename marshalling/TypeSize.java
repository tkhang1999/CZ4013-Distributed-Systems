package marshalling;

public enum TypeSize {
    INT(4),
    FLOAT(4);

    public final int value;

    private TypeSize(int value) {
        this.value = value;
    }
}
