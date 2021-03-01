package common.network;

import java.net.DatagramPacket;

public class RawMessage {
    public DatagramPacket packet;
    public byte[] buffer;
//    public String method;

    public RawMessage(DatagramPacket packet, byte[] buffer) {
        this.packet = packet;
        this.buffer = buffer;
//        this.method = method;
    }
}
