package marshalling;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Marshaller {
    private static final int INT_SIZE = TypeSize.INT.value;
    private static final int FLOAT_SIZE = TypeSize.FLOAT.value;

    public static byte[] marshal(Object obj) {
        List<Byte> message = new ArrayList<>();

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

    public static byte[] marshal(int x) {
        return new byte[]{
                (byte) (x >> 24),
                (byte) (x >> 16),
                (byte) (x >> 8),
                (byte) (x >> 0)
        };
    }

    public static byte[] marshal(float f) {
        return ByteBuffer.allocate(FLOAT_SIZE).putFloat(f).array();
    }

    public static byte[] marshal(String s) {
        byte[] ret = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            ret[i] = (byte) s.charAt(i);
        }
        return ret;
    }

    public static byte[] marshal(int[] array) {
        byte[] ret = new byte[array.length * INT_SIZE];
        for (int i = 0; i < array.length; i++) {
            byte[] num = marshal(array[i]);
            for (int j = 0; j < INT_SIZE; j++)
                ret[i * INT_SIZE + j] = num[j];
        }
        return ret;
    }

    public static byte[] marshal(float[] array) {
        byte[] ret = new byte[array.length * FLOAT_SIZE];
        for (int i = 0; i < array.length; i++) {
            byte[] num = marshal(array[i]);
            for (int j = 0; j < FLOAT_SIZE; j++)
                ret[i * FLOAT_SIZE + j] = num[j];
        }
        return ret;
    }

    public static Byte[] byteBoxing(byte[] b) {
        Byte[] ret = new Byte[b.length];
        for (int i = 0; i < b.length; i++)
            ret[i] = Byte.valueOf(b[i]);
        return ret;
    }

    public static byte[] byteUnboxing(Byte[] b) {
        byte[] ret = new byte[b.length];
        for (int i = 0; i < b.length; i++)
            ret[i] = b[i].byteValue();
        return ret;
    }

    public static byte[] byteUnboxing(List<Byte> list) {
        return byteUnboxing((Byte[]) list.toArray(new Byte[list.size()]));
    }

    public static void appendMessage(List<Byte> list, int x) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
                INT_SIZE
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
                x
        ))));
    }

    public static void appendMessage(List<Byte> list, float f) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
                FLOAT_SIZE
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
                f
        ))));
    }

    public static void appendMessage(List<Byte> list, String s) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
                s.length()
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
                s
        ))));
    }

    public static void appendMessage(List<Byte> list, int[] array) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
                INT_SIZE * array.length
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
                array
        ))));
    }

    public static void appendMessage(List<Byte> list, float[] array) {
        list.addAll(Arrays.asList(byteBoxing(marshal(
                FLOAT_SIZE * array.length
        ))));

        list.addAll(Arrays.asList(byteBoxing(marshal(
                array
        ))));
    }

    // public static void append(List list, int x) {
    //     list.addAll(Arrays.asList(byteBoxing(marshal(
    //             x
    //     ))));
    // }
}
