package main.common.network;

import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.*;
import java.util.Arrays;

public class Transport {
    DatagramSocket socket;
    int bufferLen;
    byte[] buffer;

    protected static final String STATUS = "STATUS";
    protected static final String SERVICE_ID = "SERVICEID";
    protected static final String MESSAGE_ID = "MESSAGEID";
    protected static final String REPLY = "REPLY";

    /**
     * Instantiates a new Transport instance. Called by both Client and Server.
     *
     * @param socket    the socket for packets to be sent from and received by
     * @param bufferLen the length of the buffer allocated to the contents of the received packet
     */
    public Transport(DatagramSocket socket, int bufferLen) {
        this.socket = socket;
        this.bufferLen = bufferLen;
        this.buffer = new byte[bufferLen];
    }

    /**
     * The receive method
     */
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

    /**
     * The send method
     *
     * @param dest      the destination IP address and port
     * @param packer    the BytePacker instance responsible for marshalling the content to be sent
     */
    public void send(InetSocketAddress dest, BytePacker packer) {
        byte[] msg = packer.getByteArray();
        try {
            this.socket.send(new DatagramPacket(msg, msg.length, dest));
        } catch (IOException e) {
            System.out.println(e);
        }
        return;
    }

    /**
     * The method responsible for unmarshalling the received packet and
     * checks if the received packet is intended for this specific client/server
     *
     * @param messageId     the id of the message for the check on the received packet
     */
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

    /**
     * The method responsible for unmarshalling the received packet without the check
     */
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

    /**
     * The method responsible for marshalling the request packet
     *
     * @param status    the header for the request message, it only occupies 1 byte
     * @param messageId the header for the request message, it only occupies 1 byte
     * @param reply     the payload to be sent
     */
    public final BytePacker generateReply(OneByteInt status, int messageId, String reply){

        BytePacker replyMessage = new BytePacker.Builder()
                .setProperty(STATUS, status)
                .setProperty(MESSAGE_ID, messageId)
                .setProperty(REPLY, reply)
                .build();

         return replyMessage;
    }

    /**
     * The method responsible for checking the intended message id
     *
     * @param messageId     the message id to be checked on
     * @param unpackedMsg   the unmarshalled message received
     */
    public final boolean checkMsgId(Integer messageId, ByteUnpacker.UnpackedMsg unpackedMsg) {
        Integer returnMessageId = unpackedMsg.getInteger(MESSAGE_ID);
        if (returnMessageId != null) {
            return messageId == returnMessageId;
        }
        System.out.println("Transport.checkMsgId - returnmessageid is null");
        return false;
    }

    /**
     * The method responsible for checking if the header of the message is 0
     *
     * @param unpackedMsg   the unmarshalled message received
     */
    public final boolean checkStatus(ByteUnpacker.UnpackedMsg unpackedMsg) {
        OneByteInt status = unpackedMsg.getOneByteInt(STATUS);
        System.out.println("Status: " + status.getValue());
        return status.getValue() == 0;
    }

    /**
     * The method responsible for setting a non-zero timeout on the listening socket
     * Used for monitoring clients, to catch a SocketException when the socket times out due to expired monitoring interval,
     * so that the client can proceed on with other functions
     *
     * This method is specifically for clients to receive acknowledgement packet from server upon sending a monitoring request
     *
     * @param timeout   the timeout of the socket, calculated from now till the specified end time of the monitoring interval
     * @param messageId the supposed message id of the received packet
     */
    public ByteUnpacker.UnpackedMsg setNonZeroTimeoutReceivalProcedure(int timeout, int messageId) throws MonitoringExpireException, IOException{
        try {
            this.socket.setSoTimeout(timeout);
            return this.receivalProcedure(messageId);
        } catch (SocketException e) {
            throw new MonitoringExpireException();
        }
    }

    /**
     * The method responsible for setting a non-zero timeout on the listening socket
     * Used for monitoring clients, to catch a SocketException when the socket times out due to expired monitoring interval,
     * so that the client can proceed on with other functions
     *
     * This method is specifically for clients to receive updates on a facility
     * messageIds are naturally different hence check is disabled
     *
     * @param timeout   the timeout of the socket, calculated from now till the specified end time of the monitoring interval
     */
    public ByteUnpacker.UnpackedMsg setNonZeroTimeoutReceivalProcedure(int timeout) throws MonitoringExpireException, IOException{
        try {
            this.socket.setSoTimeout(timeout);
            return this.receivalProcedure();
        } catch (SocketException e) {
            throw new MonitoringExpireException();
        }
    }

}
