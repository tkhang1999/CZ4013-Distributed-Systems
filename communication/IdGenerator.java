package communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The {@code IdGenerator} class to generate indentifiers for requests
 */
public class IdGenerator {

    private static Random randomId = new Random();
    private static Random randomDuplicateId = new Random();
    private static List<Integer> generatedId = new ArrayList<>();

    /**
     * Get a random and new identifier
     * @return An unique integer
     */
    public static int getNewId() {
        int id;

        do {
            id = randomId.nextInt(Integer.MAX_VALUE);
        } while (generatedId.contains(id));

        generatedId.add(id);
        return id;
    }

    /**
     * Get a random, new, and unsaved identifier
     * @return An unique integer
     */
    public static int getUnsavedId() {
        int id;

        do {
            id = randomId.nextInt(Integer.MAX_VALUE);
        } while (generatedId.contains(id));

        return id;
    }

    /**
     * Get a duplicate identifier. If there is no existing identifier, return -1
     * @return An integer
     */
    public static int getDuplicateId() {
        if (generatedId.isEmpty()) {
            return -1;
        } else {
            return generatedId.get(randomDuplicateId.nextInt(generatedId.size()));
        }
    }
}