package main.common.network;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;

public class RawMessage {
    public HashMap<String, Object> packet;
    public InetSocketAddress address;

    public RawMessage(HashMap<String, Object> packet, InetSocketAddress address) {
        this.packet = packet;
        this.address = address;
    }
}
