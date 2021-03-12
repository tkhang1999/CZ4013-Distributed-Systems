package marshalling;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code Marshaller} class is to marshall an actual object to a byte array
 */
public class Marshaller {
    private static final int INT_SIZE = TypeSize.INT.value;
    private static final int FLOAT_SIZE = TypeSize.FLOAT.value;

    /**
     * Marshall an object to a byte array
     * 
     * @param obj An object
     * @return A byte array
     */
    public static byte[] marshal(Object obj) {
        List<Byte> message = new ArrayList<>();

        String className = obj.getClass().getName();
        appendMessage(message, className);

        Iterable<Field> fields = Utils.getFieldsUpTo(obj.getClass(), Object.class);
        for (Field field : fields) {
            try {

                Object o = field.get(obj);
                String type = field.getGenericType().getTypeName().split("[<>]")[0];

                switch (type) {
                    case "java.lang.String":
                        appendMessage(message, (String) o);
                        break;
                    case "java.lang.Integer":
                    case "int":
                        appendMessage(message, (int) o);
                        break;
                    case "java.lang.Float":
                    case "float":
                        appendMessage(message, (float) o);
                        break;
                    case "int[]":
                        appendMessage(message, (int[]) o);
                        break;
                    case "float[]":
                        appendMessage(message, (float[]) o);
                        break;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return byteUnboxing(message);
    }

    /**
     * Marshall an integer to a byte array
     * 
     * @param target An integer
     * @return A byte array
     */
    public static byte[] marshal(int target) {
        return ByteBuffer.allocate(INT_SIZE).order(ByteOrder.BIG_ENDIAN).putInt(target).array();
    }

    /**
     * Marshall a float to a byte array
     * 
     * @param target A float
     * @return A byte array
     */
    public static byte[] marshal(float target) {
        return ByteBuffer.allocate(FLOAT_SIZE).order(ByteOrder.BIG_ENDIAN).putFloat(target).array();
    }

    /**
     * Marshall a string to a byte array
     * 
     * @param target A string
     * @return A byte array
     */
    public static byte[] marshal(String target) {
        byte[] res = new byte[target.length()];
        for (int i = 0; i < target.length(); i++) {
            res[i] = (byte) target.charAt(i);
        }
        return res;
    }

    /**
     * Marshall an integer array to a byte array
     * 
     * @param target An integer array
     * @return A byte array
     */
    public static byte[] marshal(int[] target) {
        byte[] res = new byte[target.length * INT_SIZE];
        for (int i = 0; i < target.length; i++) {
            byte[] num = marshal(target[i]);
            for (int j = 0; j < INT_SIZE; j++)
                res[i * INT_SIZE + j] = num[j];
        }
        return res;
    }

    /**
     * Marshall a float array to a byte array
     * 
     * @param target A float array
     * @return A byte array
     */
    public static byte[] marshal(float[] target) {
        byte[] res = new byte[target.length * FLOAT_SIZE];
        for (int i = 0; i < target.length; i++) {
            byte[] num = marshal(target[i]);
            for (int j = 0; j < FLOAT_SIZE; j++)
                res[i * FLOAT_SIZE + j] = num[j];
        }
        return res;
    }

    /**
     * Convert a byte array to a Byte array
     * 
     * @param b A byte array
     * @return A Byte array
     */
    public static Byte[] byteBoxing(byte[] b) {
        Byte[] ret = new Byte[b.length];
        for (int i = 0; i < b.length; i++)
            ret[i] = Byte.valueOf(b[i]);
        return ret;
    }

    /**
     * Convert a Byte array to a byte array
     * 
     * @param b A Byte array
     * @return A byte array
     */
    public static byte[] byteUnboxing(Byte[] b) {
        byte[] ret = new byte[b.length];
        for (int i = 0; i < b.length; i++)
            ret[i] = b[i].byteValue();
        return ret;
    }

    /**
     * Convert a Byte List to a byte array
     * 
     * @param b A Byte List
     * @return A byte array
     */
    public static byte[] byteUnboxing(List<Byte> b) {
        return byteUnboxing((Byte[]) b.toArray(new Byte[b.size()]));
    }

    /**
     * Append the byte array of an integer to a Byte List
     * @param list A Byte List
     * @param target An integer
     */
    public static void appendMessage(List<Byte> list, int target) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
            INT_SIZE
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
            target
        ))));
    }

    /**
     * Append the byte array of a float to a Byte List
     * @param list A Byte List
     * @param target A float
     */
    public static void appendMessage(List<Byte> list, float target) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
            FLOAT_SIZE
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
            target
        ))));
    }

    /**
     * Append the byte array of a string to a Byte List
     * @param list A Byte List
     * @param target A string
     */
    public static void appendMessage(List<Byte> list, String target) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
            target.length()
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
            target
        ))));
    }

    /**
     * Append the byte array of an integer array to a Byte List
     * @param list A Byte List
     * @param target An integer array
     */
    public static void appendMessage(List<Byte> list, int[] target) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
            INT_SIZE * target.length
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
            target
        ))));
    }

    /**
     * Append the byte array of a float array to a Byte List
     * @param list A Byte List
     * @param target A float array
     */
    public static void appendMessage(List<Byte> list, float[] target) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
            FLOAT_SIZE * target.length
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
            target
        ))));
    }
}
