package main.server;

import javafx.util.Pair;
import main.common.facility.Facilities;
import main.common.facility.NodeInformation;
import main.common.facility.Time;
import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
import main.common.network.Transport;
import main.server.History.ClientRecord;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Handler {

    protected static final String SERVICE_ID = "SERVICEID";
    protected static final String MESSAGE_ID = "MESSAGEID";
    private final History history;

    /**
     * @constructor for Handler class with a new History instance
     */
    public Handler() {
        this.history = new History();
    }

    /**
     * The entry point from ServerMain.java
     *
     * @param server     the Transport instance of the server, which handles all the routing required
     * @param facilities the Facilities instance holding all information and methods for the Facility Booking System
     * @param p          the incoming Request packet
     * @param atMostOnce the flag which determines the invocation semantics (at-most-once & at-least once)
     */
    public void handle(Transport server, Facilities facilities, DatagramPacket p, boolean atMostOnce) {
        /**
         * Deregister any expired monitoring clients whenever a packet is received by the server,
         * before handling the actual packet.
         */
        facilities.deregister();

        byte[] data = p.getData();
        InetSocketAddress clientAddr = (InetSocketAddress) p.getSocketAddress();
        int serviceRequested = data[0];

        System.out.println("Service requested by client: " + serviceRequested);

        System.out.println("Method: " + serviceRequested);

        ClientRecord client = this.history.findClient(clientAddr.getAddress(), clientAddr.getPort());

        outerloop:
        /**
         * PING method. To test network infrastructure
         */
        if (serviceRequested == Method.PING) {

            System.out.println("in ping");
            // unpack data
            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Ping.PING.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            String pingMessage = unpackedMsg.getString(Method.Ping.PING.toString());
            int messageId = unpackedMsg.getInteger(MESSAGE_ID);

            System.out.println("Received ping from client: " + pingMessage);
            System.out.println("messageId = " + messageId);

            BytePacker historicalReply = null;
            if (atMostOnce) historicalReply = client.findDuplicateMessage(messageId);

            if (historicalReply == null) {
                OneByteInt status = new OneByteInt(0);
                String reply = String.format("From main.server: ping received!\nMessage: " + pingMessage);
                BytePacker replyMessageClient = server.generateReply(status, messageId, reply);

                if (atMostOnce) {
                    client.addReplyEntry(messageId, replyMessageClient);
                    System.out.println("New reply record added for current client!");
                }

                server.send(clientAddr, replyMessageClient);
            } else {
                server.send(clientAddr, historicalReply);
            }
            System.out.println("Ping response sent to main.client.");
        }
        /**
         * QUERY method. To query on bookings on a specified facility
         */
        else if (serviceRequested == (Method.QUERY)) {

            /** IDEMPOTENT - NO HISTORY SAVING */

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Query.FACILITY.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);

            OneByteInt status = new OneByteInt(0);

            String facility = unpackedMsg.getString(Method.Query.FACILITY.toString());
            Facilities.Types t = Facilities.Types.valueOf(facility);
            ArrayList<Pair<Time, Time>> bookings = facilities.queryAvailability(t);

            String reply = parseBookingsToString(bookings);
            BytePacker replyMessageClient = server.generateReply(status, messageId, reply);

            server.send(clientAddr, replyMessageClient);
            System.out.println("Query sent to Client " + clientAddr);
        }
        /**
         * ADD method. To add a booking to a specified facility
         */
        else if (serviceRequested == Method.ADD) {

            /** IDEMPOTENT - NO HISTORY SAVING */

            // unpack data
            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.STARTDAY.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.STARTHOUR.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.STARTMIN.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.ENDDAY.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.ENDHOUR.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.ENDMIN.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Add.FACILITY.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int startDay = unpackedMsg.getInteger(Method.Add.STARTDAY.toString());
            int startHour = unpackedMsg.getInteger(Method.Add.STARTHOUR.toString());
            int startMin = unpackedMsg.getInteger(Method.Add.STARTMIN.toString());
            int endDay = unpackedMsg.getInteger(Method.Add.ENDDAY.toString());
            int endHour = unpackedMsg.getInteger(Method.Add.ENDHOUR.toString());
            int endMin = unpackedMsg.getInteger(Method.Add.ENDMIN.toString());
            String facility = unpackedMsg.getString(Method.Add.FACILITY.toString());

            Time start = new Time(startDay, startHour, startMin);
            Time end = new Time(endDay, endHour, endMin);
            Facilities.Types t = Facilities.Types.valueOf(facility);

            String uuid = facilities.addBooking(t, start, end);

            String message = uuid == null
                    ? "Booking cannot be added due to clashes with existing bookings"
                    : "Booking confirmed! UUID of booking: " + uuid +
                    " | Facility: " + facility +
                    " | Start: " + start.getDayAsName() + " " + start.hour + ":" + start.minute +
                    " | End: " + end.getDayAsName() + " " + end.hour + ":" + end.minute;

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, message);

            server.send(clientAddr, replyMessageClient);
            System.out.println("New booking uuid sent to client: " + clientAddr + " with booking uuid: " + uuid);

            callback(facilities, uuid == null ? null : t, server, status, messageId);
        }
        /**
         * CHANGE method. To shift a booking forwards or backwards without changing the duration of the booking.
         * UUID supplied
         */
        else if (serviceRequested == (Method.CHANGE)) {

            /** NON-IDEMPOTENT - HISTORY SAVING ENABLED WITH AT-MOST-ONCE SERVER */

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Change.UUID.toString(), ByteUnpacker.TYPE.STRING)
                    .setType(Method.Change.OFFSET.toString(), ByteUnpacker.TYPE.INTEGER)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);

            // check for duplicates
            if (atMostOnce) {
                BytePacker historicalReply;
                historicalReply = client.findDuplicateMessage(messageId);
                if (historicalReply != null) {
                    server.send(clientAddr, historicalReply);
                    break outerloop;
                }
            }

            String uuid = unpackedMsg.getString(Method.Change.UUID.toString());
            int offset = unpackedMsg.getInteger(Method.Change.OFFSET.toString());
            Pair<String, Facilities.Types> msg = facilities.changeBooking(uuid, offset);

            String replyMsg;
            if (msg.getValue() == null) {
                replyMsg = msg.getKey();
            } else {
                replyMsg = "UUID: " + uuid + ". " + msg.getValue().toString() + ". " + msg.getKey();
            }

            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            // add reply entry for client
            if (atMostOnce) {
                client.addReplyEntry(messageId, replyMessageClient);
                System.out.println("New reply record added for current client!");
            }

            server.send(clientAddr, replyMessageClient);
            System.out.println("Change response sent to Client " + clientAddr);

            callback(facilities, msg.getValue(), server, status, messageId);
        }
        /**
         * MONITOR method. To handle monitor request from clients on a specified facility.
         */
        else if (serviceRequested == (Method.MONITOR)) {

            /** IDEMPOTENT - NO HISTORY SAVING */

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Monitor.INTERVAL.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Monitor.FACILITY.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int monitorInterval = unpackedMsg.getInteger(Method.Monitor.INTERVAL.toString());
            String facility = unpackedMsg.getString(Method.Monitor.FACILITY.toString());
            Facilities.Types t = Facilities.Types.valueOf(facility);

            LocalDateTime end = facilities.monitorAvailability(t, monitorInterval, clientAddr);

            String replyMsg = "Monitoring " + t.toString() + " until " + end.toString();
            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            server.send(clientAddr, replyMessageClient);
            System.out.println("Monitor response sent to Client " + clientAddr);

        }
        /**
         * EXTEND method. To extend the duration of a booking forwards.
         * UUID supplied
         */
        else if (serviceRequested == (Method.EXTEND)) {

            /** NON-IDEMPOTENT - HISTORY SAVING ENABLED WITH AT-MOST-ONCE SERVER */

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Extend.UUID.toString(), ByteUnpacker.TYPE.STRING)
                    .setType(Method.Extend.EXTEND.toString(), ByteUnpacker.TYPE.DOUBLE)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);

            // check for duplicates
            if (atMostOnce) {
                BytePacker historicalReply;
                historicalReply = client.findDuplicateMessage(messageId);
                if (historicalReply != null) {
                    server.send(clientAddr, historicalReply);
                    break outerloop;
                }
            }

            String uuid = unpackedMsg.getString(Method.Extend.UUID.toString());
            double extendTime = unpackedMsg.getDouble(Method.Extend.EXTEND.toString());
            Pair<String, Facilities.Types> msg = facilities.extendBooking(uuid, extendTime);

            String replyMsg;
            if (msg.getValue() == null) {
                replyMsg = msg.getKey();
            } else {
                replyMsg = "UUID: " + uuid + ". " + msg.getValue().toString() + ". " + msg.getKey();
            }

            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            // add reply entry for client
            if (atMostOnce) {
                client.addReplyEntry(messageId, replyMessageClient);
                System.out.println("New reply record added for current client!");
            }

            server.send(clientAddr, replyMessageClient);
            System.out.println("Extend response sent to Client " + clientAddr);

            callback(facilities, msg.getValue(), server, status, messageId);
        }
        /**
         * CANCEL method. To cancel a particular booking.
         * UUID supplied
         */
        else if (serviceRequested == (Method.CANCEL)) {

            /** IDEMPOTENT - NO HISTORY SAVING */

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Cancel.UUID.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            String uuid = unpackedMsg.getString(Method.Cancel.UUID.toString());
            Pair<String, Facilities.Types> msg = facilities.cancelBooking(uuid);

            String replyMsg;
            if (msg.getValue() == null) {
                replyMsg = msg.getKey();
            } else {
                replyMsg = "UUID: " + uuid + ". " + msg.getValue().toString() + ". " + msg.getKey();
            }


            OneByteInt status = new OneByteInt(0);
            int messageId = unpackedMsg.getInteger(MESSAGE_ID);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            server.send(clientAddr, replyMessageClient);
            System.out.println("Cancel response sent to Client " + clientAddr);

            callback(facilities, msg.getValue(), server, status, messageId);
        } else {
            throw new MethodNotFoundException("Server.Handler - Method not handled");
        }

        System.out.println("-----------------");
    }

    /**
     * The callback function for methods that perform updates to a facility, to send update packets to the respective monitoring clients.
     *
     * @param facilities the Facilities instance
     * @param t          the type of Facilities updated
     * @param server     the Transport instance of the server,
     * @param status     the header for the client request message, it only occupies 1 byte
     * @param messageId  the id corresponding the method type of the request packet, and hence the response packet
     */
    private static void callback(Facilities facilities, Facilities.Types t, Transport server, OneByteInt status, int messageId) {
        if (t == null) return;

        ArrayList<NodeInformation> clientsToUpdate = facilities.clientsToUpdate(t);
        for (NodeInformation n : clientsToUpdate) {
            ArrayList<Pair<Time, Time>> bookings = facilities.queryAvailability(t);

            String reply = parseBookingsToString(bookings);
            BytePacker replyMessageClient = server.generateReply(status, messageId, reply);

            server.send(n.getAddr(), replyMessageClient);
            System.out.println("Update sent to client " + n.getAddr());
        }
    }

    /**
     * Parse ArrayList of bookings to a message string.
     *
     * @param bookings the bookings
     * @return the message string
     */
    private static String parseBookingsToString(ArrayList<Pair<Time, Time>> bookings) {
        StringBuilder sb = new StringBuilder();
        for (Pair<Time, Time> b : bookings) {
            Time start = b.getKey();
            Time end = b.getValue();
            sb.append(start.getDayAsName());
            sb.append("/");
            sb.append(start.hour);
            sb.append(":");
            sb.append(start.minute);
            sb.append("-");
            sb.append(end.getDayAsName());
            sb.append("/");
            sb.append(end.hour);
            sb.append(":");
            sb.append(end.minute);
            /** delimiter **/
            sb.append(Method.DELIMITER);
        }
        return sb.toString();
    }
}
