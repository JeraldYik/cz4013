package main.common.network;

public class Method {

    public static int PING = 0;
    public static int QUERY = 1;
    public static int ADD = 2;
    public static int CHANGE = 3;
    public static int MONITOR = 4;
    public static int CANCEL = 5;
    public static int EXTEND = 6;

    public static String DELIMITER = "#";

    public enum Query {
        FACILITY
    }

    public enum Add {
        STARTDAY,
        STARTHOUR,
        STARTMIN,
        ENDDAY,
        ENDHOUR,
        ENDMIN,
        FACILITY
    }

    public enum Change {
        UUID,
        OFFSET
    }

    public enum Monitor {
        FACILITY,
        INTERVAL
    }

    public enum Extend {
        UUID,
        EXTEND
    }

    public enum Cancel {
        UUID
    }

    public enum Ping {
        PING,
    }

}
