package main.common.facility;

import java.io.Serializable;

public class Time implements Serializable {
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
//     private Days day;
    public int day;
    public int hour;
    public int minute;

    public Time (int dayChoice, int hour, int minute) {
//        this.day = Days.values()[dayChoice];
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
                if (t1.minute >= t2.minute) return false;
                else return true;
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
        int numDays = offset/3600;
        offset -= numDays*3600;
        int numHours = offset/60;
        offset -= numHours*60;
        int numMinutes = offset;

        time.minute += numMinutes;
        if (time.minute >= 60) {
            time.hour++;
            time.minute -= 60;
        }
        time.hour += numHours;
        if (time.hour >= 24) {
            time.day++;
            time.hour -= 24;
        }
        time.day += numDays;
        if (time.day > 7) {
            return null;
        }
        return time;
    }

    public static Time subtract(Time time, int offset) {
        int numDays = offset/3600;
        offset -= numDays*3600;
        int numHours = offset/60;
        offset -= numHours*60;
        int numMinutes = offset;

        time.minute -= numMinutes;
        if (time.minute < 0) {
            time.hour--;
            time.minute += 60;
        }
        time.hour -= numHours;
        if (time.hour < 0) {
            time.day--;
            time.hour += 24;
        }
        time.day -= numDays;
        if (time.day < 1) {
            return null;
        }
        return time;
    }

//    public int getDay() {
//        return this.day;
//    }
//
//    public int getHour() {
//        return this.hour;
//    }
//
//    public int getMinute() {
//        return this.minute;
//    }
}
