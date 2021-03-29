package main.common.facility;

import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;

public class Facilities {
    public enum Types {
        LT1,
        LT2,
        MR1,
        MR2
    }

    HashMap<Types, Availability> availability;
    HashMap<Types, ArrayList<UUID>> bookings;
    PriorityQueue<LocalDateTime> timePQ;
    HashMap<LocalDateTime, NodeInformation> timeMap;
    HashMap<Types, ArrayList<NodeInformation>> monitors;

    private static class TimeComparator implements Comparator<LocalDateTime> {
        @Override
        public int compare(LocalDateTime t1, LocalDateTime t2) {
            return t1.isBefore(t2) ? -1 : 1;
        }
    }

    public Facilities() {
        this.availability = new HashMap<>();
        this.bookings = new HashMap<>();
        this.monitors = new HashMap<>();
        this.timePQ = new PriorityQueue<>(new TimeComparator());
        this.timeMap = new HashMap<>();
        for (Types t : Types.values()) {
            this.availability.put(t, new Availability());
            this.bookings.put(t, new ArrayList<>());
            this.monitors.put(t, new ArrayList<>());
        }
    }

    public HashMap<UUID, Pair<Time, Time>> queryAvailability(Types t) {
        return this.availability.get(t).queryAvailability();
    }

    public String addBooking(Types t, Time start, Time end) {
        UUID uuid = this.availability.get(t).addBooking(start, end);
        if (uuid == null) return null;
        else {
            this.bookings.get(t).add(uuid);
            return uuid.toString();
        }
    }

    public Pair<String, Facilities.Types> changeBooking(String uuid, int offset) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return new Pair<>("Confirmation ID: " + uuid + " cannot be found.", null);
        return new Pair<>(this.availability.get(t).changeBooking(uuid, offset), t);
    }


    public String cancelBooking(String uuid) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return "Confirmation ID: " + uuid + " cannot be found.";
        return this.availability.get(t).cancelBooking(uuid);
    }


    public LocalDateTime monitorAvailability(Types t, int monitorInterval, InetSocketAddress addr) {
        LocalDateTime stop = LocalDateTime.now().plusMinutes(monitorInterval);
        this.timePQ.add(stop);
        NodeInformation n = new NodeInformation(addr, t);
        this.timeMap.put(stop, n);
        this.monitors.get(t).add(n);
        return stop;
    }

    public Pair<String, Facilities.Types> extendBooking(String uuid, double extend) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return new Pair("Confirmation ID: " + uuid + " cannot be found.", null);
        return new Pair(this.availability.get(t).extendBooking(uuid, extend), t);
    }

    /** Called whenever an update occurs
     * Use this method to remove target client from cache
     */
    public void deregister() {
        System.out.println("in deregister");
        if (this.timePQ.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        ArrayList<LocalDateTime> toDeregister = new ArrayList<>();
        System.out.println("before while loop");
        while (this.timePQ.peek().isBefore(now)) {
            toDeregister.add(this.timePQ.poll());
        }
        System.out.println(toDeregister);
        for (LocalDateTime dt : toDeregister) {
            NodeInformation currentNode = this.timeMap.get(dt);
            ListIterator<NodeInformation> iter = this.monitors.get(currentNode.type).listIterator();
//            while (iter.hasNext()) {
//                if (iter.next().equals(currentNode)) iter.remove();
//            }
            this.timeMap.remove(dt);
        }

        System.out.println("before this.timePQ");
        System.out.println(this.timePQ);
        System.out.println("before this.monitors");
        System.out.println(this.monitors);
        System.out.println("before this.timemap");
        System.out.println(this.timeMap);
    }

    public ArrayList<NodeInformation> clientsToUpdate(Facilities.Types t) {
//        System.out.print("in client update ");
//        System.out.println(this.monitors.get(t).get(0).getInetSocketAddress());
        return this.monitors.get(t);
    }

    public HashMap<Types, ArrayList<NodeInformation>> getMonitors() {
        return monitors;
    }
}
