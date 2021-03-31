package main.common.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The type Byte unpacker.
 */
public class ByteUnpacker {
    private final ArrayList<String> properties;
    private final HashMap <String, TYPE> propToValue;

    /**
     * Instantiates a new Byte unpacker.
     */
    public ByteUnpacker(){
        properties = new ArrayList<>();
        propToValue = new HashMap<>();
    }

    /**
     * Parse byte array unpacked msg.
     *
     * @param data the data
     * @return the unpacked msg
     */
    public UnpackedMsg parseByteArray(byte[] data){
        int offset = 0;
        HashMap<String, Object> map = new HashMap<>();
        try{
            for(String property: properties){
                TYPE value = propToValue.get(property);
                switch(value){
                    case INTEGER:
                        map.put(property, parseInt(data, offset));
                        offset+=4;
                        break;
                    case DOUBLE:
                        map.put(property, parseDouble(data,offset));
                        offset+=8;
                        break;
                    case STRING:
                        int length = parseInt(data, offset);
                        map.put(property, parseString(data, offset + 4, length));
                        offset += 4 + length;
                        break;
                    case BYTE_ARRAY:
                        int byte_length = parseInt(data, offset);
                        map.put(property, Arrays.copyOfRange(data, offset + 4, offset + 4 + byte_length));
                        break;
                    case ONE_BYTE_INT:
                        map.put(property, new OneByteInt(data[offset] & 0xFF));
                        offset += 1;
                        break;
                }
            }
            UnpackedMsg result = new UnpackedMsg(map);
            return result;
        }catch(Exception e){
            return null;
        }
    }

    private String parseString(byte[] data, int offset, int length) {
        try{
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<length;i++,offset++){
                sb.append((char)data[offset]);
            }
            return sb.toString();
        } catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    private Double parseDouble(byte[] data, int offset) {
        int doubleSize = 8;
        byte[] temp = new byte[doubleSize];
        for(int i =0;i<doubleSize;i++){
            temp[i] = data[offset+i];
        }
        double value = ByteBuffer.wrap(temp).getDouble();
        return value;
    }

    private Integer parseInt(byte[] data, int offset) {
        int intSize = 4;
        byte[] temp = new byte[intSize];
        for(int i=0;i<intSize;i++){
            temp[i] = data[offset+i];
        }

        int value = ByteBuffer.wrap(temp).getInt();
        return value;
    }

    /**
     * The type Unpacked msg.
     */
    public static class UnpackedMsg{
        private final HashMap<String, Object> map;

        /**
         * Instantiates a new Unpacked msg.
         *
         * @param map the map
         */
        public UnpackedMsg(HashMap<String,Object> map){
            this.map = map;
        }

        /**
         * Get integer integer.
         *
         * @param key the key
         * @return the integer
         */
        public Integer getInteger(String key){
            if(map.containsKey(key) && (map.get(key) instanceof Integer)){
                return (Integer) map.get(key);
            }
            return null;
        }

        /**
         * Get string string.
         *
         * @param key the key
         * @return the string
         */
        public String getString(String key){
            if(map.containsKey(key) && map.get(key) instanceof String){
                return (String) map.get(key);
            }
            return null;
        }

        /**
         * Get double double.
         *
         * @param key the key
         * @return the double
         */
        public Double getDouble(String key){
            if(map.containsKey(key) && map.get(key) instanceof Double){
                return (Double) map.get(key);
            }
            return null;
        }

        /**
         * Get byte array byte [ ].
         *
         * @param value the value
         * @return the byte [ ]
         */
        public byte[] getByteArray(String value) {
            if (map.containsKey(value) && map.get(value) instanceof byte[]) {
                return (byte[]) map.get(value);
            }
            return null;
        }

        /**
         * Gets one byte int.
         *
         * @param value the value
         * @return the one byte int
         */
        public OneByteInt getOneByteInt(String value) {
            if (map.containsKey(value) && map.get(value) instanceof OneByteInt) {
                return (OneByteInt) map.get(value);
            }
            return null;
        }
    }

    /**
     * The enum Type.
     */
    public enum TYPE {
        /**
         * Integer type.
         */
        INTEGER,
        /**
         * Double type.
         */
        DOUBLE,
        /**
         * String type.
         */
        STRING,
        /**
         * Byte array type.
         */
        BYTE_ARRAY,
        /**
         * One byte int type.
         */
        ONE_BYTE_INT
    }

    /**
     * The type Builder.
     */
    public static class Builder{
        private final ByteUnpacker unpacker;

        /**
         * Instantiates a new Builder.
         */
        public Builder(){
            unpacker = new ByteUnpacker();

        }

        /**
         * Set type builder.
         *
         * @param property the property
         * @param type     the type
         * @return the builder
         */
        public Builder setType(String property, TYPE type){
            unpacker.properties.add(property);
            unpacker.propToValue.put(property, type);
            return this;
        }

        /**
         * Build byte unpacker.
         *
         * @return the byte unpacker
         */
        public ByteUnpacker build(){
            return unpacker;
        }
    }
}