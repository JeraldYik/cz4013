package main.server;

import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
import main.common.network.RawMessage;
import main.common.network.Transport;
import javafx.util.Pair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

public class Handler {

    public static void handle(Transport server, Facilities facilities, RawMessage req) {
        String method = (String) req.packet.get(Method.METHOD);
        System.out.println("Method: " + method);

        if (method.equals(Method.Methods.PING.toString())) {
            System.out.println("message received from main.client: ");
            System.out.println(req.packet);

            String reply = "From main.server: " + req.packet.get("payload");
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.PING, reply));
            System.out.println("message sent to main.client.");
        }

        else if (method.equals(Method.Methods.QUERY.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            Facilities.Types t = (Facilities.Types) o.get(Method.Query.FACILITY.toString());
            HashMap<UUID, Pair<Time, Time>> bookings = facilities.queryAvailability(t);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, bookings));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.ADD.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            Facilities.Types t = (Facilities.Types) o.get(Method.Add.FACILITY.toString());
            Time start = (Time) o.get(Method.Add.START.toString());
            Time end = (Time) o.get(Method.Add.END.toString());
            String uuid = facilities.addBooking(t, start, end);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.ADD, uuid));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.CHANGE.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            String uuid = (String) o.get(Method.Change.UUID.toString());
            int offset = (Integer) o.get(Method.Change.OFFSET.toString());
            String msg = facilities.changeBooking(uuid, offset);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, msg));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.MONITOR.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            Facilities.Types t = (Facilities.Types) o.get(Method.Monitor.FACILITY.toString());
            int monitorInterval = (Integer) o.get(Method.Monitor.INTERVAL.toString());
            String clientAddr = (String) o.get(Method.Monitor.CLIENTADDR.toString());
            int clientPort = (Integer) o.get(Method.Monitor.CLIENTPORT.toString());
            LocalDateTime end = facilities.monitorAvailability(t, monitorInterval, clientAddr, clientPort);
            String msg = "Monitoring " + t.toString() + " until " + end.toString();
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.MONITOR, msg));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.EXTEND.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            String uuid = (String) o.get(Method.Extend.UUID.toString());
            double extend = (Double) o.get(Method.Extend.EXTEND.toString());
            String msg = facilities.extendBooking(uuid, extend);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.EXTEND, msg));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.CANCEL.toString())) {
            System.out.println("Query received from main.client");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            String uuid = (String) o.get(Method.Cancel.UUID.toString());

            String msg = facilities.cancelBooking(uuid);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CANCEL, msg));
            System.out.println(("Query sent to main.client."));
        }

        else {
            throw new MethodNotFoundException("Server.Handler - Method not handled");
        }

        System.out.println("-----------------");
    }
}
