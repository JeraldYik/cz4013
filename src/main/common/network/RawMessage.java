package main.common.network;

import java.net.SocketAddress;
import java.util.HashMap;

public class RawMessage {
    public HashMap<String, Object> packet;
    public SocketAddress address;

    public RawMessage(HashMap<String, Object> packet, SocketAddress address) {
        this.packet = packet;
        this.address = address;
    }
}
