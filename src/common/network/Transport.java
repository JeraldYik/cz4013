package common.network;

import java.io.IOException;
import java.net.*;

/** IMPORTANT:
 *  Assume that request from client is resolved before new requests from other clients are sent (from lab manual)
 *  therefore, there is no need for a queue
 */

public class Transport {
    DatagramSocket socket;
    int bufferLen;
    byte[] buffer;

    public Transport(DatagramSocket socket, int bufferLen) {
        this.socket = socket;
        this.bufferLen = bufferLen;
        this.buffer = new byte[bufferLen];
    }

    public RawMessage receive() {
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            this.socket.receive(packet);
            System.out.println("Length of response: " + packet.getLength() + " bytes.");
            String msg = new String(this.buffer, 0, packet.getLength());
            System.out.println("Message: " + msg);
            RawMessage raw = new RawMessage(packet, buffer.clone());
            // reset buffer
            buffer = new byte[this.bufferLen];
            return raw;
        } catch (IOException e) {
            System.out.println("IO Exception! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Serialise obj next time
//    public <T> void send(InetAddress destAddr, int destPort, T payload) {
    public void send(SocketAddress dest, String payload) {
        try {
            /** TODO:
             *  To create an object for serialisation
             */
            byte[] buf = payload.getBytes();
            this.socket.send(new DatagramPacket(buf, buf.length, dest));
        } catch (IOException e) {
            System.out.println("IO Exception! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
