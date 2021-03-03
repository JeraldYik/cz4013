package common.network;

import java.net.SocketAddress;
import java.util.HashMap;

public class RawMessage {
    public HashMap<String, String> obj;
    public SocketAddress address;
//    public String method;

    public RawMessage(HashMap<String, String> obj, SocketAddress address) {
        this.obj = obj;
        this.address = address;
//        this.method = method;
    }
}
