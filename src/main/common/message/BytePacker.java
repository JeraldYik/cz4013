package main.common.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.nio.ByteBuffer;

public class BytePacker {

    private ArrayList<String> properties;
    private HashMap<String, Object> propToValue;

    private BytePacker() {
        this.properties = new ArrayList<>();
        this.propToValue = new HashMap<>();
    }

    // Store object into hashmap based on property key
    private void setValue(String property, Object value){
        this.properties.add(property);
        this.propToValue.put(property, value);
    }

    public Object getValue(String property) {
        return propToValue.get(property);
    }

    public HashMap<String, Object> getPropToValue() { return this.propToValue; }


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

        for (Object value : propToValue.values()) {
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
            Object value = propToValue.get(property);
            if (value instanceof Integer) {
                index = intToByte((Integer) value, buffer, index);                      /* convert Integer to bytes */
            } else if (value instanceof String) {
                index = intToByte(((String) value).length(), buffer, index);            /* first integer is the length of string */
                index = stringToByte((String) value, buffer, index);                    /* convert the content of string to bytes */
            } else if (value instanceof Double) {
                index = doubleToByte((Double) value, buffer, index);                        /* convert Long to bytes */
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

        byte[] temp = new byte[4];
        ByteBuffer.wrap(temp).putInt(i);
        for(byte b: temp){
            buffer[index++] = b;
        }

        return index;                                       /* update and return current index number */
    }

    private int stringToByte(String s, byte[] buffer, int index) {
        for (byte b : s.getBytes()) {
            buffer[index++] = b;
        }
        return index;
    }

    private int doubleToByte(Double d, byte[] buffer, int index) {
        byte[] temp = new byte[8];
        ByteBuffer.wrap(temp).putDouble(d);
        for(byte b: temp){
            buffer[index++] = b;
        }
        return index;
    }

    public static class Builder{
        private BytePacker packer;

        public Builder(){
            packer = new BytePacker();
        }

        public Builder setProperty(String key, int value){
            return set(key,value);
        }

        public Builder setProperty(String key, double value){
            return set(key, value);
        }

        public Builder setProperty(String key, String string){
            return set(key, string);
        }

        public Builder setProperty(String key, OneByteInt value){
            return set(key,value);
        }

        public Builder set(String key, Object value){
            packer.setValue(key,value);
            return this;
        }


        public BytePacker build(){
            return packer;
        }
    }
}
