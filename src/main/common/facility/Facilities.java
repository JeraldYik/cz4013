package main.common.facility;

import javafx.util.Pair;
import main.common.network.NodeInformation;

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
        if (uuid == null) return "Booking for " + t.toString() + " already exist at " + start + "-" + end;
        else {
            this.bookings.get(t).add(uuid);
            return uuid.toString();
        }
    }

    public String changeBooking(String uuid, int offset) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return "Confirmation ID: " + uuid + " cannot be found.";
        return this.availability.get(t).changeBooking(uuid, offset);
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


    public LocalDateTime monitorAvailability(Types t, int monitorInterval, String clientAddr, int clientPort) {
        LocalDateTime stop = LocalDateTime.now().plusMinutes(monitorInterval);
        this.timePQ.add(stop);
        NodeInformation n = new NodeInformation(clientAddr, clientPort);
        this.timeMap.put(stop, n);
        this.monitors.get(t).add(n);
        return stop;
    }

    public String extendBooking(String uuid, double extend) {

        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return "Confirmation ID: " + uuid + " cannot be found.";
        return this.availability.get(t).extendBooking(uuid, extend);
    }

    /** Called whenever an update occurs
     * Use this method to send deregister packet to target client
     * and remove target client from cache
     */
//    public String deregister() {
//
//    }
}
