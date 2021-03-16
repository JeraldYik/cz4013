package main.common.facility;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Facilities {
    public enum Types {
        LT1,
        LT2,
        MR1,
        MR2
    }

    HashMap<Types, Availability> availability;
    HashMap<Types, ArrayList<UUID>> bookings;

    public Facilities() {
        this.availability = new HashMap<>();
        this.bookings = new HashMap<>();
        for (Types t : Types.values()) {
            this.availability.put(t, new Availability());
            this.bookings.put(t, new ArrayList<>());
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

}
