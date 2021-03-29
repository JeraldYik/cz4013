package main.common.network;

import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;


import javax.swing.*;
import java.awt.desktop.SystemSleepEvent;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/** IMPORTANT:
 *  Assume that request from main.client is resolved before new requests from other clients are sent (from lab manual)
 *  therefore, there is no need for a queue
 */

public class Transport {
    DatagramSocket socket;
    int bufferLen;
    byte[] buffer;


    protected static final String STATUS = "status";
    protected static final String SERVICE_ID = "serviceId";
    protected static final String MESSAGE_ID = "messageId";
    protected static final String REPLY = "reply";

//    private double failureRate;
//    private int id;
//    private int invocationSemantics;
//    private int maxTimeout;
//    private int timeout;
//    private HashMap<Integer, Boolean> handledReponse;

    public Transport(DatagramSocket socket, int bufferLen) {
        this.socket = socket;
        this.bufferLen = bufferLen;
        this.buffer = new byte[bufferLen];
    }

    public DatagramPacket receive() throws IOException {
        Arrays.fill(this.buffer, (byte) 0);
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);

        this.socket.receive(packet);
        return packet;
//            System.out.println("Length of response: " + packet.getLength() + " bytes.");
//            // de-seralizing
//            HashMap<String, Object> res = (HashMap<String, Object>) Deserializer.deserialize(this.buffer);
//            RawMessage raw = new RawMessage(res, packet.getSocketAddress());
//            // reset buffer
//            buffer = new byte[this.bufferLen];
//            return raw;

    }

    // Serialise obj next time
    public void send(SocketAddress dest, BytePacker packer) {
        System.out.println("Message sent to: " + dest);
        byte[] msg = packer.getByteArray();
        try {
            this.socket.send(new DatagramPacket(msg, msg.length, dest));
        } catch (IOException e) {
            System.out.println(e);
        }
        return;
    }


//    public void send(BytePacker packer) throws IOException {
//        byte[] msg = packer.getByteArray();
//        DatagramPacket p = new DatagramPacket(msg, msg.length);
//
//        this.socket.send(p);
//        return;
//    }

//    public DatagramPacket receive() throws IOException {
//        Arrays.fill(buffer, (byte) 0);
//        DatagramPacket p = new DatagramPacket(buffer, buffer.length)
//        this.socket.receive(p);
//        return p;
//    }

    public final ByteUnpacker.UnpackedMsg receivalProcedure(SocketAddress socketAddress, BytePacker packer, int messageId) throws IOException, SocketTimeoutException {
        while(true) {
            try {
                DatagramPacket reply = this.receive();
                ByteUnpacker byteUnpacker = new ByteUnpacker.Builder()
                        .setType(STATUS, ByteUnpacker.TYPE.ONE_BYTE_INT)
                        .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                        .setType(REPLY, ByteUnpacker.TYPE.STRING)
                        .build();

                ByteUnpacker.UnpackedMsg unpackedMsg = byteUnpacker.parseByteArray(reply.getData());

                if (checkMsgId(messageId, unpackedMsg)) {
                    return unpackedMsg;
                }

            }
            catch(IOException e) {
                System.out.println(e);
                this.send(socketAddress, packer);
            }
        }
    }

    public final BytePacker generateReply(OneByteInt status, int messageId, String reply){

        BytePacker replyMessage = new BytePacker.Builder()
                .setProperty(STATUS, status)
                .setProperty(MESSAGE_ID, messageId)
                .setProperty(REPLY, reply)
                .build();

         return replyMessage;
    }

    public final boolean checkMsgId(Integer messageId, ByteUnpacker.UnpackedMsg unpackedMsg) {
        Integer returnMessageId = unpackedMsg.getInteger(MESSAGE_ID);
        System.out.println("returnMessageId: " + returnMessageId);
        System.out.println("messageId: " + messageId);
        if (returnMessageId != null) {
            return messageId == returnMessageId;
        }
        return false;
    }

    public final boolean checkStatus(ByteUnpacker.UnpackedMsg unpackedMsg) {
        OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
        System.out.println("Status: " + status.getValue());
        if (status.getValue() == 0) return true;
        return false;
    }

}
