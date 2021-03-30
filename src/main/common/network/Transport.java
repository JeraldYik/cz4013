package main.common.network;

import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;


import javax.swing.*;
import java.awt.datatransfer.Clipboard;
import java.awt.desktop.SystemSleepEvent;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.*;
import java.util.HashMap;
import java.util.Arrays;

/** IMPORTANT:
 *  Assume that request from main.client is resolved before new requests from other clients are sent (from lab manual)
 *  therefore, there is no need for a queue
 */

public class Transport {
    DatagramSocket socket;
    int bufferLen;
    byte[] buffer;

    protected static final String STATUS = "STATUS";
    protected static final String SERVICE_ID = "SERVICEID";
    protected static final String MESSAGE_ID = "MESSAGEID";
    protected static final String REPLY = "REPLY";

    public Transport(DatagramSocket socket, int bufferLen) {
        this.socket = socket;
        this.bufferLen = bufferLen;
        this.buffer = new byte[bufferLen];
    }

    public DatagramPacket receive() throws IOException {
        Arrays.fill(this.buffer, (byte) 0);
        DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length);

        try {
            this.socket.receive(packet);
        } catch (SocketTimeoutException e) {
            throw new MonitoringExpireException();
        }
        return packet;
    }


    public void send(InetSocketAddress dest, BytePacker packer) {
        byte[] msg = packer.getByteArray();
        try {
            this.socket.send(new DatagramPacket(msg, msg.length, dest));
        } catch (IOException e) {
            System.out.println(e);
        }
        return;
    }

    public ByteUnpacker.UnpackedMsg receivalProcedure(int messageId) throws IOException {
        while (true) {
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
                } else {
                    throw new StreamCorruptedException();
                }
            } catch (StreamCorruptedException e) {
                System.out.println("Request and Reply IDs don't match!");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    /** Overloaded method for broadcasted updates to monitoring clients **/
    /** checkMsgId disabled **/
    public ByteUnpacker.UnpackedMsg receivalProcedure() {
        while (true) {
            try {
                DatagramPacket reply = this.receive();
                ByteUnpacker byteUnpacker = new ByteUnpacker.Builder()
                        .setType(STATUS, ByteUnpacker.TYPE.ONE_BYTE_INT)
                        .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                        .setType(REPLY, ByteUnpacker.TYPE.STRING)
                        .build();

                return byteUnpacker.parseByteArray(reply.getData());

            } catch (IOException e) {
                System.out.println(e);
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
        if (returnMessageId != null) {
            return messageId == returnMessageId;
        }
        System.out.println("Transport.checkMsgId - returnmessageid is null");
        return false;
    }

    public final boolean checkStatus(ByteUnpacker.UnpackedMsg unpackedMsg) {
        OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
        System.out.println("Status: " + status.getValue());
        if (status.getValue() == 0) return true;
        return false;
    }

    /** timeout in milliseconds **/
    public ByteUnpacker.UnpackedMsg setNonZeroTimeoutReceivalProcedure(int timeout, int messageId) throws MonitoringExpireException, IOException{
        try {
            this.socket.setSoTimeout(timeout);
            return this.receivalProcedure(messageId);
        } catch (SocketException e) {
            throw new MonitoringExpireException();
        }
    }

    /** Overloaded method for broadcasted updates to monitoring clients **/
    public ByteUnpacker.UnpackedMsg setNonZeroTimeoutReceivalProcedure(int timeout) throws MonitoringExpireException, IOException{
        try {
            this.socket.setSoTimeout(timeout);
            return this.receivalProcedure();
        } catch (SocketException e) {
            throw new MonitoringExpireException();
        }
    }

}
