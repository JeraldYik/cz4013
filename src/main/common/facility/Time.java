package main.common.facility;

public class Time {
    /**
     * the availability of the facility over seven days of a week (the
     * time is to be represented in the form of day/hour/minute, where day is of enumerated
     * type with possible values from Monday to Sunday, and hour and minute are integers),
     */
    public enum Days {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    public int day;
    public int hour;
    public int minute;

    public Time (int dayChoice, int hour, int minute) {
        this.day = dayChoice;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString() {
        return Days.values()[this.day-1] + "/" + this.hour + "/" + this.minute;
    }

    public static boolean compare(Time t1, Time t2) {
        if (t1.day > t2.day) return false;
        else if (t1.day == t2.day) {
            if (t1.hour > t2.hour) return false;
            else if (t1.hour == t2.hour) {
                return t1.minute < t2.minute;
            } else return true;
        } else return true;
    }

    public static Time getMax(Time t1, Time t2) {
        if (compare(t1, t2)) return t2;
        else return t1;
    }

    public static Time getMin(Time t1, Time t2) {
        if (compare(t1, t2)) return t1;
        else return t2;
    }

    public static Time add(Time time, int offset) {
        int day = time.day;
        int hour = time.hour;
        int min = time.minute;

        int numDays = offset/3600;
        offset -= numDays*3600;
        int numHours = offset/60;
        offset -= numHours*60;
        int numMinutes = offset;

        min += numMinutes;
        while (min >= 60) {
            hour++;
            min -= 60;
        }
        hour += numHours;
        while (hour >= 24) {
            day++;
            hour -= 24;
        }
        day += numDays;
        if (day > 7) {
            return null;
        }
        return new Time(day, hour, min);
    }

    public static Time subtract(Time time, int offset) {
        int day = time.day;
        int hour = time.hour;
        int min = time.minute;

        int numDays = offset/3600;
        offset -= numDays*3600;
        int numHours = offset/60;
        offset -= numHours*60;
        int numMinutes = offset;

        min -= numMinutes;
        while (min < 0) {
            hour--;
            min += 60;
        }
        hour -= numHours;
        while (hour < 0) {
            day--;
            hour += 24;
        }
        day -= numDays;
        if (day < 1) {
            return null;
        }
        return new Time(day, hour, min);
    }

    public String getDayAsName() {
        return Days.values()[this.day - 1].toString();
    }
}
