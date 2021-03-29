package main.server;

import javafx.util.Pair;
import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
import main.common.network.Transport;
import main.server.History.ClientRecord;
import java.net.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class AtMostOnceHandler {

    protected static final String STATUS = "status";
    protected static final String SERVICE_ID = "serviceId";
    protected static final String MESSAGE_ID = "messageId";
    protected static final String REPLY = "reply";
    private History history;

    public AtMostOnceHandler() { this.history = new History(); }

    public void handle(Transport server, Facilities facilities, DatagramPacket p) {

        facilities.deregister();
        byte[] data = p.getData();
        InetAddress clientAddr = p.getAddress();
        int clientPort = p.getPort();
        int serviceRequested = data[0];

        System.out.println("Service requested by client: " + serviceRequested);

        System.out.println("Method: " + serviceRequested);

        ClientRecord client = this.history.findClient(clientAddr, clientPort);

        if (serviceRequested == Method.PING) {

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

            BytePacker historicalReply = client.findDuplicateMessage(messageId);

            if (historicalReply == null) {
                OneByteInt status = new OneByteInt(0);
                String reply = String.format("From main.server: ping received!\nMessage: " + pingMessage);
                BytePacker replyMessageClient = server.generateReply(status, messageId, reply);
                client.addReplyEntry(messageId, replyMessageClient);
                System.out.println("New reply record added for current client!");
                server.send(new InetSocketAddress(clientAddr, clientPort), replyMessageClient);
            }
            else {
                server.send(new InetSocketAddress(clientAddr, clientPort), historicalReply);
            }
            System.out.println("Ping response sent to main.client.");
        }

//        else if (serviceRequested == (Method.QUERY)) {
//
//            ByteUnpacker unpacker = new ByteUnpacker.Builder()
//                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
//                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
//                    .setType("facility", ByteUnpacker.TYPE.STRING)
//                    .build();
//
//            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);
//
//            String facility = unpackedMsg.getString("facility");
//            Facilities.Types t = Facilities.Types.valueOf(facility);
//
//
//            // incomplete
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            Facilities.Types t = (Facilities.Types) o.get(Method.Query.FACILITY.toString());
//            HashMap<UUID, Pair<Time, Time>> bookings = facilities.queryAvailability(t);
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, bookings));
//            System.out.println("Query sent to main.client.");
//        }

        else if (serviceRequested == Method.ADD) {

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

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, uuid);

            server.send(new InetSocketAddress(clientAddr, clientPort), replyMessageClient);

            System.out.println("New booking uuid sent to main.client.");
//            callback(facilities, uuid == null ? null : t, server);

        }

//        else if (serviceRequested == (Method.CHANGE)) {
//
//
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            String uuid = (String) o.get(Method.Change.UUID.toString());
//            int offset = (Integer) o.get(Method.Change.OFFSET.toString());
//            Pair<String, Facilities.Types> msg = facilities.changeBooking(uuid, offset);
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, msg.getKey()));
//            System.out.println("Query sent to main.client.");
//
//            callback(facilities, msg.getValue(), server);
//        }

        else if (serviceRequested == (Method.MONITOR)) {

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Monitor.INTERVAL.toString(), ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Monitor.FACILITY.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int monitorInterval = unpackedMsg.getInteger("monitorInterval");
            String facility = unpackedMsg.getString("facility");
            Facilities.Types t = Facilities.Types.valueOf(facility);
            String clientAddress = clientAddr.toString();

            LocalDateTime end = facilities.monitorAvailability(t, monitorInterval, new InetSocketAddress(clientAddress, clientPort));

            String replyMsg = "Monitoring " + t.toString() + " until " + end.toString();
            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            server.send(new InetSocketAddress(clientAddr, clientPort), replyMessageClient);

            System.out.println("Monitor response sent to main.client.");

        }

        else if (serviceRequested == (Method.EXTEND)) {

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Extend.UUID.toString(), ByteUnpacker.TYPE.STRING)
                    .setType(Method.Extend.EXTEND.toString(), ByteUnpacker.TYPE.DOUBLE)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            String uuid = unpackedMsg.getString("uuid");
            double extendTime = unpackedMsg.getDouble("extendTime");
            Pair<String, Facilities.Types> msg = facilities.extendBooking(uuid, extendTime);
            String replyMsg = "";

            if (((Pair) msg).getValue() == null){
                replyMsg = "Extension failed";
            } else {
                replyMsg = "UUID: "+msg.getKey()+ " extend " + msg.getValue() + " booking success!";

            }

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            server.send(new InetSocketAddress(clientAddr, clientPort), replyMessageClient);

            System.out.println("Extend response sent to main.client");
//            callback(facilities, msg.getValue(), server);
        }

        else if (serviceRequested == (Method.CANCEL)) {

            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType(Method.Cancel.UUID.toString(), ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            String uuid = unpackedMsg.getString("uuid");
            Pair<String, Facilities.Types> msg = facilities.cancelBooking(uuid);
            String replyMsg = "";

            if (msg.getValue() == null){
                replyMsg = "Extension failed";
            } else {
                replyMsg = "UUID: "+msg.getKey()+ " cancel " + msg.getValue() + "booking success!";

            }

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, replyMsg);

            server.send(new InetSocketAddress(clientAddr, clientPort), replyMessageClient);

            System.out.println("Cancel response sent to main.client");
//            callback(facilities, msg.getValue(), server);

        }

        else {
            throw new MethodNotFoundException("Server.Handler - Method not handled");
        }

        System.out.println("-----------------");
    }

}