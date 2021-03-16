package main.server;

import main.common.facility.Facilities;
import main.common.facility.Time;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
import main.common.network.RawMessage;
import main.common.network.Transport;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.UUID;

public class Handler {

    public static void handle(Transport server, Facilities facilities, RawMessage req) {
        String method = (String) req.packet.get("method");
        System.out.println("Method: " + method);

        if (method.equals(Method.Methods.PING.toString())) {
            System.out.println("Message received from main.client: ");
            System.out.println(req.packet);

            String reply = "From main.server: " + req.packet.get("payload");
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.PING, reply));
            System.out.println("Message sent to main.client.");
        }

        else if (method.equals(Method.Methods.QUERY.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            Facilities.Types t = (Facilities.Types) req.packet.get("payload");
            HashMap<UUID, Pair<Time, Time>> bookings = facilities.queryAvailability(t);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.QUERY, bookings));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.ADD.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get("payload");
            Facilities.Types t = (Facilities.Types) o.get("facility");
            Time start = (Time) o.get("start");
            Time end = (Time) o.get("end");
            String uuid = facilities.addBooking(t, start, end);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.ADD, uuid));
            System.out.println("Query sent to main.client.");
        }

        else if (method.equals(Method.Methods.CHANGE.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get("payload");
            String uuid = (String) o.get("uuid");
            int offset = (Integer) o.get("offset");
            String msg = facilities.changeBooking(uuid, offset);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, msg));
            System.out.println("Query sent to main.client.");
        }

        else {
            throw new MethodNotFoundException("Server.Handler - Method not handled");
        }

        System.out.println("-----------------");
    }
}
