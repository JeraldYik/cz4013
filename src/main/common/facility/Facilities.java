package main.common.facility;

import javafx.util.Pair;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;

/**
 * The type Facilities.
 */
public class Facilities {
    /**
     * The enum Types.
     */
    public enum Types {
        /**
         * Lt 1 types.
         */
        LT1,
        /**
         * Lt 2 types.
         */
        LT2,
        /**
         * Mr 1 types.
         */
        MR1,
        /**
         * Mr 2 types.
         */
        MR2
    }

    private final HashMap<Types, Availability> availability;
    private final HashMap<Types, ArrayList<UUID>> bookings;
    private final PriorityQueue<LocalDateTime> timePQ;
    private final HashMap<LocalDateTime, NodeInformation> timeMap;
    private final HashMap<Types, ArrayList<NodeInformation>> monitors;

    private static class TimeComparator implements Comparator<LocalDateTime> {
        @Override
        public int compare(LocalDateTime t1, LocalDateTime t2) {
            return t1.isBefore(t2) ? -1 : 1;
        }
    }

    /**
     * Instantiates a new Facilities.
     */
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

    /**
     * Query availability array list.
     *
     * @param t the t
     * @return the array list
     */
    public ArrayList<Pair<Time, Time>> queryAvailability(Types t) {
        return new ArrayList(this.availability.get(t).queryAvailability().values());
    }

    /**
     * Return null if unsuccessful  @param t the t
     *
     * @param start the start
     * @param end   the end
     * @return the string
     */
    public String addBooking(Types t, Time start, Time end) {
        UUID uuid = this.availability.get(t).addBooking(start, end);
        System.out.println("Starting time: " + start);
        System.out.println("Ending time: " + end);
        if (uuid == null) return null;
        else {
            this.bookings.get(t).add(uuid);
            return uuid.toString();
        }
    }

    /**
     * Return Facility Type null if unsuccessful  @param uuid the uuid
     *
     * @param offset the offset
     * @return the pair
     */
    public Pair<String, Facilities.Types> changeBooking(String uuid, int offset) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) { return new Pair("UUID: " + uuid + " cannot be found.", null); }
        Pair<String, Boolean> result = this.availability.get(t).changeBooking(uuid, offset);
        return new Pair(result.getKey(), result.getValue() == null ? null : t);
    }

    /**
     * Return Facility Type null if unsuccessful  @param uuid the uuid
     *
     * @return the pair
     */
    public Pair<String, Facilities.Types> cancelBooking(String uuid) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return new Pair("UUID: " + uuid + " cannot be found.", null);
        Pair<String, Boolean> result = this.availability.get(t).cancelBooking(uuid);
        return new Pair(result.getKey(), result.getValue() == null ? null : t);
    }

    /**
     * Monitor availability local date time.
     *
     * @param t               the t
     * @param monitorInterval the monitor interval
     * @param addr            the addr
     * @return the local date time
     */
    public LocalDateTime monitorAvailability(Types t, int monitorInterval, InetSocketAddress addr) {
        LocalDateTime stop = LocalDateTime.now().plusMinutes(monitorInterval);
        this.timePQ.add(stop);
        NodeInformation n = new NodeInformation(addr, t);
        this.timeMap.put(stop, n);
        this.monitors.get(t).add(n);
        System.out.println("Registering " + addr + " on " + t.toString());
        return stop;
    }

    /**
     * Return Facility Type null if unsuccessful  @param uuid the uuid
     *
     * @param extend the extend
     * @return the pair
     */
    public Pair<String, Facilities.Types> extendBooking(String uuid, double extend) {
        Types t = null;
        for (HashMap.Entry<Types, ArrayList<UUID>> entry : this.bookings.entrySet()) {
            for (UUID u : entry.getValue()) {
                if (u.toString().equals(uuid)) t = entry.getKey();
            }
        }
        if (t == null) return new Pair("Confirmation ID: " + uuid + " cannot be found.", null);
        Pair<String, Boolean> result = this.availability.get(t).extendBooking(uuid, extend);
        return new Pair(result.getKey(), result.getValue() == null ? null : t);
    }

    /**
     * Called whenever an update occurs
     * Use this method to remove target client from cache
     */
    public void deregister() {
        if (this.timePQ.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        ArrayList<LocalDateTime> toDeregister = new ArrayList<>();
        while (!this.timePQ.isEmpty() && this.timePQ.peek().isBefore(now)) {
            toDeregister.add(this.timePQ.poll());
        }
        for (LocalDateTime dt : toDeregister) {
            NodeInformation currentNode = this.timeMap.get(dt);
            ListIterator<NodeInformation> iter = this.monitors.get(currentNode.type).listIterator();
            while (iter.hasNext()) {
                if (iter.next().equals(currentNode)) iter.remove();
            }
            System.out.println("Deregistered " + this.timeMap.get(dt).getAddr());
            this.timeMap.remove(dt);
        }
    }

    /**
     * Clients to update array list.
     *
     * @param t the t
     * @return the array list
     */
    public ArrayList<NodeInformation> clientsToUpdate(Facilities.Types t) {
        return this.monitors.get(t);
    }
}
