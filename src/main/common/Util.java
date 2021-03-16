package main.common;

import main.common.network.Method;

import java.util.HashMap;

public class Util {
    public static HashMap<String, Object> putInHashMapPacket(Method.Methods method, Object payload) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("method", method.toString());
        map.put("payload", payload);
        return map;
    }
}
