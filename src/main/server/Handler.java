package main.server;

import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.message.BytePacker;
import main.common.message.ByteUnpacker;
import main.common.message.OneByteInt;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
//import main.common.network.RawMessage;
import main.common.network.Transport;
import javafx.util.Pair;
import java.net.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class Handler {

    protected static final String STATUS = "status";
    protected static final String SERVICE_ID = "serviceId";
    protected static final String MESSAGE_ID = "messageId";
    protected static final String REPLY = "reply";

    public static void handle(Transport server, Facilities facilities, DatagramPacket p) {

        byte[] data = p.getData();
        InetAddress clientAddr = p.getAddress();
        int clientPort = p.getPort();
        int serviceRequested = data[0];

        System.out.println("Service requested by client: " + serviceRequested);

        System.out.println("Method: " + serviceRequested);

        if (serviceRequested == Method.PING) {

            // unpack data
            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                    .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                    .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                    .setType("pingMessage", ByteUnpacker.TYPE.STRING)
                    .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            String pingMessage = unpackedMsg.getString("pingMessage");
            System.out.println("Received message: " + pingMessage);
            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            System.out.println("Received service: " + serviceRequested);

            OneByteInt status = new OneByteInt(0);
            String reply = String.format("From main.server: Ping received");

            BytePacker replyMessageClient = server.generateReply(status, messageId, reply);

            server.send(new InetSocketAddress(clientAddr, clientPort) ,replyMessageClient);

            System.out.println("Ping response sent to main.client.");
        }

//        else if (serviceRequested == (Method.QUERY)) {
//            System.out.println("Query received from main.client: ");
//            System.out.println(req.packet);
//
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            Facilities.Types t = (Facilities.Types) o.get(Method.Query.FACILITY.toString());
//            HashMap<UUID, Pair<Time, Time>> bookings = facilities.queryAvailability(t);
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, bookings));
//            System.out.println("Query sent to main.client.");
//        }
//
        else if (serviceRequested == Method.ADD) {

            // unpack data
            ByteUnpacker unpacker = new ByteUnpacker.Builder()
                     .setType(SERVICE_ID, ByteUnpacker.TYPE.ONE_BYTE_INT)
                     .setType(MESSAGE_ID, ByteUnpacker.TYPE.INTEGER)
                     .setType("startDay", ByteUnpacker.TYPE.INTEGER)
                     .setType("startHour", ByteUnpacker.TYPE.INTEGER)
                     .setType("startMin", ByteUnpacker.TYPE.INTEGER)
                     .setType("endDay", ByteUnpacker.TYPE.INTEGER)
                     .setType("endHour", ByteUnpacker.TYPE.INTEGER)
                     .setType("endMin", ByteUnpacker.TYPE.INTEGER)
                     .setType("facility", ByteUnpacker.TYPE.STRING)
                     .build();

            ByteUnpacker.UnpackedMsg unpackedMsg = unpacker.parseByteArray(data);

            int startDay = unpackedMsg.getInteger("startDay");
            int startHour = unpackedMsg.getInteger("startHour");
            int startMin = unpackedMsg.getInteger("startMin");
            int endDay = unpackedMsg.getInteger("endDay");
            int endHour = unpackedMsg.getInteger("endHour");
            int endMin = unpackedMsg.getInteger("endMin");
            String facility = unpackedMsg.getString("facility");

            Time start = new Time(startDay, startHour, startMin);
            Time end = new Time(endDay, endHour, endMin);
            Facilities.Types t = Facilities.Types.valueOf(facility);

            String uuid = facilities.addBooking(t, start, end);

            int messageId = unpackedMsg.getInteger(MESSAGE_ID);
            OneByteInt status = new OneByteInt(0);

            BytePacker replyMessageClient = server.generateReply(status, messageId, uuid);

            server.send(new InetSocketAddress(clientAddr, clientPort), replyMessageClient);

            System.out.println("New booking uuid sent to main.client.");
        }
//
//        else if (serviceRequested == (Method.CHANGE)) {
//            System.out.println("Query received from main.client: ");
//            System.out.println(req.packet);
//
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            String uuid = (String) o.get(Method.Change.UUID.toString());
//            int offset = (Integer) o.get(Method.Change.OFFSET.toString());
//            String msg = facilities.changeBooking(uuid, offset);
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, msg));
//            System.out.println("Query sent to main.client.");
//        }
//
//        else if (serviceRequested == (Method.MONITOR)) {
//            System.out.println("Query received from main.client: ");
//            System.out.println(req.packet);
//
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            Facilities.Types t = (Facilities.Types) o.get(Method.Monitor.FACILITY.toString());
//            int monitorInterval = (Integer) o.get(Method.Monitor.INTERVAL.toString());
//            String clientAddr = (String) o.get(Method.Monitor.CLIENTADDR.toString());
//            int clientPort = (Integer) o.get(Method.Monitor.CLIENTPORT.toString());
//            LocalDateTime end = facilities.monitorAvailability(t, monitorInterval, clientAddr, clientPort);
//            String msg = "Monitoring " + t.toString() + " until " + end.toString();
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.MONITOR, msg));
//            System.out.println("Query sent to main.client.");
//        }
//
//        else if (serviceRequested == (Method.EXTEND)) {
//            System.out.println("Query received from main.client: ");
//            System.out.println(req.packet);
//
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            String uuid = (String) o.get(Method.Extend.UUID.toString());
//            double extend = (Double) o.get(Method.Extend.EXTEND.toString());
//            String msg = facilities.extendBooking(uuid, extend);
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.EXTEND, msg));
//            System.out.println("Query sent to main.client.");
//        }
//
//        else if (serviceRequested == (Method.CANCEL)) {
//            System.out.println("Query received from main.client");
//            System.out.println(req.packet);
//
//            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
//            String uuid = (String) o.get(Method.Cancel.UUID.toString());
//
//            String msg = facilities.cancelBooking(uuid);
//            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CANCEL, msg));
//            System.out.println(("Query sent to main.client."));
//        }

        else {
            throw new MethodNotFoundException("Server.Handler - Method not handled");
        }

        System.out.println("-----------------");
    }

}
