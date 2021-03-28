package main.common.facility;

import javafx.util.Pair;

import java.util.*;

public class Availability {

    private HashMap<UUID, Pair<Time, Time>> bookings;
//    private List<ArrayList<Pair<Time, Time>>> availability;

    public Availability() {
//        this.availability = Arrays.asList(new ArrayList<>(7));
//        for (ArrayList<Pair<Time, Time>> day : this.availability) {
//            day.add(new Pair<>(new Time(1,0,0), new Time(7,23,59)));
//        }
        this.bookings = new HashMap<>();
    }

    /** TODO: print out the bookings in a more visual format, minus the uuid (for one or multiple days) **/
    public HashMap<UUID, Pair<Time, Time>> queryAvailability() {
        return this.bookings;
//        return this.availability;
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

//        /** Update availability metadata**/
//        int dayCounter = start.day;
//        /** Push latest availability to start of new booking **/
//        if (dayCounter != end.day) {
//            ArrayList<Pair<Time,Time>> currentDayAvail = this.availability.get(dayCounter-1);
//            Pair<Time, Time> latestAvail = currentDayAvail.get(currentDayAvail.size() - 1);
//            latestAvail.getValue().hour = start.hour;
//            latestAvail.getValue().minute = start.minute;
//            dayCounter++;
//        }
//        /** Fill up entire day **/
//        while (dayCounter < end.day) {
//            ArrayList<Pair<Time,Time>> currentDayAvail = this.availability.get(dayCounter-1);
//            currentDayAvail.clear();
//            dayCounter++;
//        }
//
//        ArrayList<Pair<Time,Time>> currentDayAvail = this.availability.get(dayCounter-1);
//        Pair<Time, Time> earliestAvail = currentDayAvail.get(0);
//        earliestAvail.getValue().hour = end.hour;
//        earliestAvail.getValue().minute = end.minute;

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
                return "Booking with supplied offset cannot be updated due to overlapping with existing bookings";
            }
        }

        // finally update metadata
        this.bookings.put(u_uuid, new Pair<>(newStart, newEnd));

        return "Booking updated";
    }

    /** The change modifies the length of the time period booked **/
    public String extendBooking(String uuid, double extend) {
        if (this.bookings.isEmpty()) return "Booking cannot be found";
        UUID u_uuid = UUID.fromString(uuid);
        Pair<Time, Time> foundBooking = this.bookings.get(u_uuid);
        if (foundBooking == null) return "Booking cannot be found";

        Time newStart = foundBooking.getKey();
        int extendMin = (int) extend * 60;
        // do the extension
        Time newEnd = Time.add(foundBooking.getValue(), extendMin);
        if (newEnd == null) return "Booking exceeds time frame of the week";

        // remove first further overlap calculation
        this.bookings.remove(u_uuid);

        // check for overlap
        for (Pair<Time, Time> b : this.bookings.values()) {
            if (Time.compare(Time.getMax(b.getKey(), newStart), Time.getMin(b.getValue(), newEnd))) {
                this.bookings.put(u_uuid, new Pair<>(foundBooking.getKey(), foundBooking.getValue()));
                return "Booking with supplied extension cannot be updated due to overlapping with existing bookings";
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
