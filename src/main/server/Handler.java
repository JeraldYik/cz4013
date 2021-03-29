package main.server;

import javafx.util.Pair;
import main.common.facility.Facilities;
import main.common.facility.NodeInformation;
import main.common.facility.Time;
import main.common.network.Method;
import main.common.network.MethodNotFoundException;
import main.common.network.RawMessage;
import main.common.network.Transport;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Handler {

    private History history;

    public void handle(Transport server, Facilities facilities, RawMessage req) {
        /** Deregister first **/
        this.history = new History();
        ArrayList<NodeInformation> deregisters = facilities.deregister();
        while (deregisters != null && !deregisters.isEmpty()) {
            NodeInformation n = deregisters.remove(deregisters.size() - 1);
            SocketAddress addr = new InetSocketAddress(n.getAddress(), n.getPort());
            server.send(addr, main.common.Util.putInHashMapPacket(Method.Methods.MONITOR, "Deregistering..."));
        }

        String method = (String) req.packet.get(Method.METHOD);
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

            callback(facilities, t, server);
        }

        else if (method.equals(Method.Methods.CHANGE.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            String uuid = (String) o.get(Method.Change.UUID.toString());
            int offset = (Integer) o.get(Method.Change.OFFSET.toString());
            Pair<String, Facilities.Types> msg = facilities.changeBooking(uuid, offset);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.CHANGE, msg.getKey()));
            System.out.println("Query sent to main.client.");

            callback(facilities, msg.getValue(), server);
        }

        else if (method.equals(Method.Methods.MONITOR.toString())) {
            System.out.println("Query received from main.client: ");
            System.out.println(req.packet);

            HashMap<String, Object> o = (HashMap<String, Object>) req.packet.get(Method.PAYLOAD);
            Facilities.Types t = (Facilities.Types) o.get(Method.Monitor.FACILITY.toString());
            int monitorInterval = (Integer) o.get(Method.Monitor.INTERVAL.toString());

            String clientAddr = ((InetSocketAddress) req.address).getAddress().toString();
            int clientPort = ((InetSocketAddress) req.address).getPort();
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
            Pair<String, Facilities.Types> msg = facilities.extendBooking(uuid, extend);
            server.send(req.address, main.common.Util.putInHashMapPacket(Method.Methods.EXTEND, msg.getKey()));
            System.out.println("Query sent to main.client.");

            callback(facilities, msg.getValue(), server);
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

    private static void callback(Facilities facilities, Facilities.Types t, Transport server) {
        if (t == null) return;
        ArrayList<NodeInformation> clientsToUpdate = facilities.clientsToUpdate(t);
        while (!clientsToUpdate.isEmpty()) {
            NodeInformation n = clientsToUpdate.remove(clientsToUpdate.size() - 1);
            SocketAddress addr = new InetSocketAddress(n.getAddress(), n.getPort());
            /** TODO: to change to updated availability record instead **/
            HashMap<UUID, Pair<Time, Time>> bookings = facilities.queryAvailability(t);
            server.send(addr, main.common.Util.putInHashMapPacket(Method.Methods.MONITOR, bookings));
        }
    }
}
