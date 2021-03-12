package communication;

public enum InvocationMethod {
    AT_LEAST_ONCE(false),
    AT_MOST_ONCE(true);

    public final boolean filterDuplicates;

    private InvocationMethod(boolean filterDuplicates) {
        this.filterDuplicates = filterDuplicates;
    }
}