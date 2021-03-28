package main.common.network;

import main.common.serialize.Deserializer;
import main.common.serialize.Serializer;

import main.server.message.BytePacker;


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

    private double failureRate;
    private int id;
    private int invocationSemantics;
    private int maxTimeout;
    private int timeout;
    private HashMap<Integer, Boolean> handledReponse;

    public Transport(DatagramSocket socket, int bufferLen) {
        this.socket = socket;
        this.bufferLen = bufferLen;
        this.buffer = new byte[bufferLen];
        this.failureRate = Constants.DEFAULT_FAIL_RATE;
        this.invocationSemantics = Constants.NORMAL_INVO;
        this.handledReponse = new HashMap<Integer, Boolean>();
    }

    public double getFailureRate(){
        return this.failureRate;
    }

    public void setFailureRate(double failureRate){
        this.failureRate = failureRate;
    }

    public int getInvocationSemantics(){
        return this.invocationSemantics;
    }

    public void setInvocationSemantics(int invocationSemantics){
        this.invocationSemantics = invocationSemantics;
    }

    public int getTimeout(){
        return this.timeout;
    }

    public void setTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
        this.timeout = timeout;
    }

    public int getID() {
        this.id++;
        return this.id;
    }

    public RawMessage receive() {
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
            System.out.println("Transport.receive - IO Exception! " + e.getMessage());
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            System.out.println("Transport.receive - Class Not Found Exception! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Serialise obj next time
    public void send(SocketAddress dest, Object payload) {
        try {
            // serializing
            byte[] buf = Serializer.serialize(payload);
            this.socket.send(new DatagramPacket(buf, buf.length, dest));

        } catch (IOException e) {
            System.out.println("Transport.send - IO Exception! " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendPack(SocketAddress dest, BytePacker packer) throws IOException{
        this.socket.send(packer, dest, );
    }
}
