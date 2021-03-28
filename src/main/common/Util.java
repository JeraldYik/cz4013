package main.common;

import main.common.network.Method;

import java.util.HashMap;

public class Util {
    public static HashMap<String, Object> putInHashMapPacket(Method.Methods method, Object payload) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(Method.METHOD, method.toString());
        map.put(Method.PAYLOAD, payload);
        return map;
    }
}
