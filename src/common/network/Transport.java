package common.network;

import common.serialize.Deserializer;
import common.serialize.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

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
            // deseralising
            HashMap<String, String> res = (HashMap<String, String>) Deserializer.deserialize(this.buffer);
            RawMessage raw = new RawMessage(res, packet.getSocketAddress());
            // reset buffer
            buffer = new byte[this.bufferLen];
            return raw;
        } catch (IOException e) {
            System.out.println("IO Exception! " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found Exception! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Serialise obj next time
    public void send(SocketAddress dest, Object payload) {
        try {
            // serialisating
            byte[] buf = Serializer.serialize(payload);
            this.socket.send(new DatagramPacket(buf, buf.length, dest));
        } catch (IOException e) {
            System.out.println("IO Exception! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
