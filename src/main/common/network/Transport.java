package main.common.network;

import main.common.serialize.Deserializer;
import main.common.serialize.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

/** IMPORTANT:
 *  Assume that request from main.client is resolved before new requests from other clients are sent (from lab manual)
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

    public RawMessage receive(){
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            this.socket.receive(packet);
//            System.out.println("Length of response: " + packet.getLength() + " bytes.");
            // de-seralizing
            HashMap<String, Object> res = (HashMap<String, Object>) Deserializer.deserialize(this.buffer);
            RawMessage raw = new RawMessage(res, (InetSocketAddress) packet.getSocketAddress());
            // reset buffer
            buffer = new byte[this.bufferLen];
            return raw;
        } catch (SocketTimeoutException e) {
          throw new MonitoringExpireException();
        } catch (IOException e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Serialise obj next time
    public void send(InetSocketAddress dest, Object payload) {
        try {
            // serializing
            byte[] buf = Serializer.serialize(payload);
            this.socket.send(new DatagramPacket(buf, buf.length, dest));
        } catch (IOException e) {
            System.out.println("Transport.send - " + e.getClass().toString() + ": " + e.getMessage());
        }
    }

    /** timeout in milliseconds **/
    public RawMessage setNonZeroTimeoutAndReceive(int timeout) throws MonitoringExpireException{
        try {
            this.socket.setSoTimeout(timeout);
            return this.receive();
        } catch (SocketException e) {
            throw new MonitoringExpireException();
        }

    }
}
