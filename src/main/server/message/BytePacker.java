package main.server.message;
import java.util.ArrayList;
import java.util.HashMap;

public class BytePacker {

    private ArrayList<String> properties;
    private HashMap<String, Object> values;

    private BytePacker() {
        this.properties = new ArrayList<>();
        this.values = new HashMap<>();
    }

    // Store object into hashmap based on property key
    private void setValue(String property, Object value){
        this.properties.add(property);
        this.values.put(property, value);
    }

    public Object getValue(String property) {
        return values.get(property);
    }

    public byte[] getByteArray(){
        /*
            Calculate the size required for the byte array
            based on the type of the object
            Integer     4 bytes
            String      4 + length of string bytes
            Long        8 bytes
            Byte Array  4 + length of byte array
            OneByteInt  1 byte
         */
        int size = 1;

        for (Object value : values.values()) {
            if (value instanceof Integer) {
                size += 4;
            } else if (value instanceof String) {
                size += 4 + ((String) value).length();
            } else if (value instanceof Long) {
                size += 8;
            } else if (value instanceof byte[]) {
                size += 4 + ((byte[]) value).length;
            } else if (value instanceof OneByteInt) {
                size += 1;
            }
        }

        byte[] buffer = new byte[size];

        int index = 0;
        for (String property : properties) {
            Object value = values.get(property);
            if (value instanceof Integer) {
                index = intToByte((Integer) value, buffer, index);                      /* convert Integer to bytes */
            } else if (value instanceof String) {
                index = intToByte(((String) value).length(), buffer, index);            /* first integer is the length of string */
                index = stringToByte((String) value, buffer, index);                    /* convert the content of string to bytes */
            } else if (value instanceof Long) {
                index = longToByte((Long) value, buffer, index);                        /* convert Long to bytes */
            } else if (value instanceof byte[]) {
                index = intToByte(((byte[]) value).length, buffer, index);              /* first integer is the length of the byte array */
                System.arraycopy(value, 0, buffer, index, ((byte[]) value).length);     /* store the bytes into the buffer */
                index += ((byte[]) value).length;
            } else if (value instanceof OneByteInt) {
                buffer[index++] = (byte) (((OneByteInt) value).getValue() & 0xFF);     /* convert OneByteInt into bytes */
            }
        }

        return buffer;
    }

    private int intToByte(int i, byte[] buffer, int index) {
        buffer[index++] = (byte) ((i >> 24) & 0xFF);         /* most left byte, byte 3 */
        buffer[index++] = (byte) ((i >> 16) & 0xFF);         /* byte 2 */
        buffer[index++] = (byte) ((i >> 8) & 0xFF);          /* byte 1 */
        buffer[index++] = (byte) ((i) & 0xFF);               /* most right byte, byte 0 */
        return index;                                       /* update and return current index number */
    }

    private int stringToByte(String s, byte[] buffer, int index) {
        for (byte b : s.getBytes()) {
            buffer[index++] = b;
        }
        return index;
    }

    private int longToByte(long l, byte[] buffer, int index) {
        buffer[index++] = (byte) ((l >> 56) & 0xFF);    /* byte 7 */
        buffer[index++] = (byte) ((l >> 48) & 0xFF);    /* byte 6 */
        buffer[index++] = (byte) ((l >> 40) & 0xFF);    /* byte 5 */
        buffer[index++] = (byte) ((l >> 32) & 0xFF);    /* byte 4 */
        buffer[index++] = (byte) ((l >> 24) & 0xFF);    /* byte 3 */
        buffer[index++] = (byte) ((l >> 16) & 0xFF);    /* byte 2 */
        buffer[index++] = (byte) ((l >> 8) & 0xFF);    /* byte 1 */
        buffer[index++] = (byte) ((l) & 0xFF);    /* byte 0 */
        return index;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean has = false;
        for (String property : properties) {
            if (has) sb.append(", ");
            Object value = values.get(property);
            if (value instanceof Integer) {
                sb.append(property).append(": ").append(value);
            } else if (value instanceof String) {
                sb.append(property).append(": \"").append(((String) value)).append("\"");
            } else if (value instanceof Long) {
                sb.append(property).append(": ").append(value);
            } else if (value instanceof byte[]) {
                sb.append(property).append(": <byte array>");
            } else if (value instanceof OneByteInt) {
                sb.append(property).append(": ").append(((OneByteInt) value).getValue());
            }
            has = true;
        }
        sb.append("}");
        return sb.toString();
    }

    public static class Builder {
        private BytePacker bytePacker;

        public Builder() {
            bytePacker = new BytePacker();
        }

        public Builder setValue(String property, Long value) {
            return set(property, value);
        }

        public Builder setValue(String property, Integer value) {
            return set(property, value);
        }

        public Builder setValue(String property, String value) {
            return set(property, value);
        }

        public Builder setValue(String property, byte[] value) {
            return set(property, value);
        }

        public Builder setValue(String property, OneByteInt value) {
            return set(property, value);
        }

        private Builder set(String property, Object value) {
            bytePacker.setValue(property, value);
            return this;
        }

        public BytePacker build() {
            return bytePacker;
        }
    }
}
