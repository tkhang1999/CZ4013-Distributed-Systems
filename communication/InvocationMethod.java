package communication;

/**
 * The {@code InvocationMethod} enum for invocation method of the server
 */
public enum InvocationMethod {
    AT_LEAST_ONCE(false),
    AT_MOST_ONCE(true);

    public final boolean filterDuplicates;

    private InvocationMethod(boolean filterDuplicates) {
        this.filterDuplicates = filterDuplicates;
    }
}