package main.common.facility;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.UUID;

public class Availability {

    // change to hashmap
    private HashMap<UUID, Pair<Time, Time>> bookings;

    public Availability() {
        this.bookings = new HashMap<>();
    }

    /** TODO: print out the bookings in a more visual format, minus the uuid (for one or multiple days) **/
    public HashMap<UUID, Pair<Time, Time>> queryAvailability() {
        return this.bookings;
    }

    public UUID addBooking(Time start, Time end) {
        if (!this.bookings.isEmpty()) {
            // check for overlap
            for (Pair<Time, Time> b : this.bookings.values()) {
                // https://leetcode.com/problems/my-calendar-i/discuss/109475/JavaC%2B%2B-Clean-Code-with-Explanation
                // max(current.start, start) < min(current.end, end)
                if (Time.compare(Time.getMax(b.getKey(), start), Time.getMin(b.getValue(), end))) {
                    return null;
                }
            }
        }
        UUID uuid = UUID.randomUUID();
        this.bookings.put(uuid, new Pair<>(start, end));
        return uuid;
    }

    /** The change does not modify the length of the time period booked **/
    public String changeBooking(String uuid, int offset) {
        if (this.bookings.isEmpty()) return "Booking cannot be found";
        UUID u_uuid = UUID.fromString(uuid);
        Pair<Time, Time> foundBooking = this.bookings.get(u_uuid);
        if (foundBooking == null) return "Booking cannot be found";

        Time newStart = foundBooking.getKey();
        Time newEnd = foundBooking.getValue();
        // do the offset
        if (offset > 0) {
            newEnd = Time.add(foundBooking.getValue(), offset);
            if (newEnd == null) return "Booking exceeds time frame of the week";
            newStart = Time.add(foundBooking.getKey(), offset);
        } else if (offset < 0) {
            newStart = Time.subtract(foundBooking.getKey(), -offset);
            if (newStart == null) return "Booking exceeds time frame of the week";
            newEnd = Time.subtract(foundBooking.getValue(), -offset);
        }

        // remove first further overlap calculation
        this.bookings.remove(u_uuid);

        // check for overlap
        for (Pair<Time, Time> b : this.bookings.values()) {
            if (Time.compare(Time.getMax(b.getKey(), newStart), Time.getMin(b.getValue(), newEnd))) {
                this.bookings.put(u_uuid, new Pair<>(foundBooking.getKey(), foundBooking.getValue()));
                return "Booking with supplied offset cannot be updated due to existing bookings";
            }
        }

        // finally update metadata
        this.bookings.put(u_uuid, new Pair<>(newStart, newEnd));
        return "Booking updated";
    }

    public String cancelBooking(String uuid) {
        if (this.bookings.isEmpty()) return "Booking cannot be found";
        UUID u_uuid = UUID.fromString(uuid);
        Pair<Time, Time> foundBooking = this.bookings.get(u_uuid);
        if (foundBooking == null) return "Booking cannot be found";

        this.bookings.remove(u_uuid);
        return "Booking removed";
    }
}
