package main.common.facility;

import javafx.util.Pair;

import java.util.*;

public class Availability {

    private HashMap<UUID, Pair<Time, Time>> bookings;

    public Availability() {
        this.bookings = new HashMap<>();
    }

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
        this.bookings.put(uuid, new Pair(start, end));

        return uuid;
    }

    /** The change does not modify the length of the time period booked **/
    public Pair<String, Boolean> changeBooking(String uuid, int offset) {
        if (this.bookings.isEmpty()) return new Pair("Failure! Booking cannot be found.", false);
        UUID u_uuid = UUID.fromString(uuid);
        Pair<Time, Time> foundBooking = this.bookings.get(u_uuid);
        if (foundBooking == null) return new Pair("Failure! Booking cannot be found.", false);

        Time newStart = foundBooking.getKey();
        Time newEnd = foundBooking.getValue();
        // do the offset
        if (offset > 0) {
            newEnd = Time.add(foundBooking.getValue(), offset);
            if (newEnd == null) return new Pair("Failure! Booking exceeds time frame of the week.", false);
            newStart = Time.add(foundBooking.getKey(), offset);
        } else if (offset < 0) {
            newStart = Time.subtract(foundBooking.getKey(), Math.abs(offset));
            if (newStart == null) return new Pair("Failure! Booking exceeds time frame of the week.", false);
            newEnd = Time.subtract(foundBooking.getValue(), Math.abs(offset));
        }

        // remove first for overlap calculation
        this.bookings.remove(u_uuid);

        // check for overlap
        for (Pair<Time, Time> b : this.bookings.values()) {
            if (Time.compare(Time.getMax(b.getKey(), newStart), Time.getMin(b.getValue(), newEnd))) {
                this.bookings.put(u_uuid, new Pair<>(foundBooking.getKey(), foundBooking.getValue()));
                return new Pair("Failure! Booking with supplied offset cannot be updated due to overlapping with existing bookings.", false);
            }
        }

        // finally update metadata
        this.bookings.put(u_uuid, new Pair<>(newStart, newEnd));

        return new Pair("Success! Booking updated.", true);
    }

    /** The change modifies the length of the time period booked **/
    public Pair<String, Boolean> extendBooking(String uuid, double extend) {
        if (this.bookings.isEmpty()) return new Pair("Failure! Booking cannot be found.", false);
        UUID u_uuid = UUID.fromString(uuid);
        Pair<Time, Time> foundBooking = this.bookings.get(u_uuid);
        if (foundBooking == null) return new Pair("Failure! Booking cannot be found.", false);

        Time newStart = foundBooking.getKey();
        int extendMin = (int) extend * 60;
        // do the extension
        Time newEnd = Time.add(foundBooking.getValue(), extendMin);
        System.out.println(newEnd);
        if (newEnd == null) return new Pair("Failure! Booking exceeds time frame of the week.", false);

        // remove first further overlap calculation
        this.bookings.remove(u_uuid);

        // check for overlap
        for (Pair<Time, Time> b : this.bookings.values()) {
            if (Time.compare(Time.getMax(b.getKey(), newStart), Time.getMin(b.getValue(), newEnd))) {
                this.bookings.put(u_uuid, new Pair<>(foundBooking.getKey(), foundBooking.getValue()));
                return new Pair("Failure! Booking with supplied extension cannot be updated due to overlapping with existing bookings.", false);
            }
        }

        // finally update metadata
        this.bookings.put(u_uuid, new Pair<>(newStart, newEnd));
        return new Pair("Success! Booking updated.", true);
    }

    public Pair<String, Boolean> cancelBooking(String uuid) {
        if (this.bookings.isEmpty()) return new Pair("Failure! Booking cannot be found.", false);
        UUID u_uuid = UUID.fromString(uuid);
        Pair<Time, Time> foundBooking = this.bookings.get(u_uuid);
        if (foundBooking == null) return new Pair("Failure! Booking cannot be found.", false);

        this.bookings.remove(u_uuid);
        return new Pair("Success! Booking removed.", true);
    }
}
