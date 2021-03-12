package marshalling;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * The {@code Unmarshaller} class is to unmarshall a byte-array data to an actual object 
 */
public class Unmarshaller {

    private static final int INT_SIZE = TypeSize.INT.value;
    private static final int FLOAT_SIZE = TypeSize.FLOAT.value;

    /**
     * Set a field of an object to its true value
     * 
     * @param object An object
     * @param fieldName Name of the field to be set
     * @param fieldValue Value of the field
     */
    public static void set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Unmarshall a byte array to return an object
     * 
     * @param b A byte array
     * @return An object
     */
    public static Object unmarshal(byte[] b) {
        int ptr = 0;
        Class<?> clazz = null;
        Object obj = null;

        int sourceLength = unmarshalInteger(b, ptr);
        ptr += INT_SIZE;
        String className = unmarshalString(b, ptr, ptr + sourceLength);
        
        ptr += sourceLength;

        try {
            clazz = Class.forName(className);
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Iterable<Field> fields =  Utils.getFieldsUpTo(clazz, Object.class);

        for (Field field : fields) {

            String type = field.getGenericType().getTypeName().split("[<>]")[0];

            sourceLength = unmarshalInteger(b, ptr);
            ptr += INT_SIZE;

            switch (type) {
                case "java.lang.String":
                    String stringValue = unmarshalString(b, ptr, ptr + sourceLength);
                    ptr += sourceLength;
                    set(obj, field.getName(), stringValue);
                    break;
                case "java.lang.Integer":
                case "int":
                    int intValue = unmarshalInteger(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), intValue);
                    break;
                case "java.lang.Float":
                case "float":
                    float floatValue = unmarshalFloat(b, ptr);
                    ptr += sourceLength;
                    set(obj, field.getName(), floatValue);
                    break;
                case "int[]":
                    int[] intArrValue = unmarshalIntArray(b, ptr, ptr + sourceLength);
                    ptr += sourceLength;
                    set(obj, field.getName(), intArrValue);
                    break;
                case "float[]":
                    float[] floatArrValue = unmarshalFloatArray(b, ptr, ptr + sourceLength);
                    ptr += sourceLength;
                    set(obj, field.getName(), floatArrValue);
                    break;
            }
        }
        return obj;
    }

    /**
     * Unmarshall a byte array of an interger
     * 
     * @param b A byte array
     * @param start Start index
     * @return An interger
     */
    public static int unmarshalInteger(byte[] b, int start) {
        // return b[start] << 24 | (b[start + 1] & 0xFF) << 16 | (b[start + 2] & 0xFF) << 8 | (b[start + 3] & 0xFF);
        byte[] content = new byte[]{
            b[start], b[start + 1], b[start + 2], b[start + 3]
        };
        return ByteBuffer.wrap(content).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * Unmarshall a byte array of a float
     * 
     * @param b A byte array
     * @param start Start index
     * @return A float
     */
    public static float unmarshalFloat(byte[] b, int start) {
        byte[] content = new byte[]{
                b[start], b[start + 1], b[start + 2], b[start + 3]
        };
        return ByteBuffer.wrap(content).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    /**
     * Unmarshall a byte array of a string
     * 
     * @param b A byte array
     * @param start Start index
     * @param end End index
     * @return A string
     */
    public static String unmarshalString(byte[] b, int start, int end) {
        char[] c = new char[end - start];
        for (int i = start; i < end; i++) {
            c[i - start] = (char) (b[i]);
        }
        return new String(c);
    }

    /**
     * Unmarshall a byte array of an integer array 
     * 
     * @param b A byte array
     * @param start Start index
     * @param end End index
     * @return An integer array
     */
    public static int[] unmarshalIntArray(byte[] b, int start, int end) {
        int[] array = new int[(end - start) / INT_SIZE];
        for (int i = 0; i < array.length; i++) {
            int startIndex = start + i * INT_SIZE;
            array[i] = unmarshalInteger(Arrays.copyOfRange(b, startIndex, startIndex + INT_SIZE), 0);
        }
        return array;
    }

    /**
     * Unmarshall a byte array of a float array
     * 
     * @param b A byte array
     * @param start Start index
     * @param end End index
     * @return A float array
     */
    public static float[] unmarshalFloatArray(byte[] b, int start, int end) {
        float[] array = new float[(end - start) / FLOAT_SIZE];
        for (int i = 0; i < array.length; i++) {
            int startIndex = start + i * FLOAT_SIZE;
            array[i] = unmarshalFloat(Arrays.copyOfRange(b, startIndex, startIndex + FLOAT_SIZE), 0);
        }
        return array;
    }
}
