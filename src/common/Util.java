package common;

import java.util.HashMap;

public class Util {
    public static HashMap<String, String> putInHashMapPacket(String method, String message) {
        HashMap<String, String> map = new HashMap<>();
        map.put("method", method);
        map.put("message", message);
        return map;
    }
}
