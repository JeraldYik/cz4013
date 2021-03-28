package main.common.network;

import main.common.serialize.Deserializer;
import main.common.serialize.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Random;

/** IMPORTANT:
 *  Assume that request from main.client is resolved before new requests from other clients are sent (from lab manual)
 *  therefore, there is no need for a queue
 */

public class Transport {
    DatagramSocket socket;
    int bufferLen;
    byte[] buffer;
    private final double probability = 0.3;
    private final int timeout = 2000;
    private Random random;

    public Transport(DatagramSocket socket, int bufferLen) {
        this.socket = socket;
        this.bufferLen = bufferLen;
        this.buffer = new byte[bufferLen];
        this.random = new Random();
    }

    public RawMessage receive() throws SocketTimeoutException {
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        // adding packet loss probability
        try {
            if (random.nextDouble() < probability) {
                this.socket.receive(packet);
            } else {
                System.out.println("Blocking packet reception!");
                Thread.sleep(timeout);
                throw new SocketTimeoutException();
            }
            System.out.println("Length of response: " + packet.getLength() + " bytes.");
            // de-seralizing
            HashMap<String, Object> res = (HashMap<String, Object>) Deserializer.deserialize(this.buffer);
            RawMessage raw = new RawMessage(res, packet.getSocketAddress());
            // reset buffer
            buffer = new byte[this.bufferLen];
            return raw;
        } catch (SocketTimeoutException e) {
//            System.out.println("SocketTimeoutException thrown in Transport!");
            throw new SocketTimeoutException();
        } catch (IOException e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

        // Serialise obj next time
    public void send(SocketAddress dest, Object payload) {
        try {
            // serializing
            byte[] buf = Serializer.serialize(payload);
//            SocketAddress d = new InetSocketAddress("")
            System.out.println("this.socket.getInetAddress() " + this.socket.getInetAddress());
            System.out.println("dest " + dest);
            this.socket.send(new DatagramPacket(buf, buf.length, dest));
        } catch (IOException e) {
            System.out.println("Transport.send - " + e.getClass().toString() + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Transport.send - " + e.getClass().toString() + ": " + e.getMessage());
            System.out.println(e);
        }
    }

    public RawMessage serverReceive() {
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);
        try {
            this.socket.receive(packet);
            System.out.println("Length of response: " + packet.getLength() + " bytes.");
            // de-seralizing
            HashMap<String, Object> res = (HashMap<String, Object>) Deserializer.deserialize(this.buffer);
            RawMessage raw = new RawMessage(res, packet.getSocketAddress());
            // reset buffer
            buffer = new byte[this.bufferLen];
            return raw;
        } catch (IOException e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Transport.receive - " + e.getClass().toString() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
