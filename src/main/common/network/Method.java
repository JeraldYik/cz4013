package main.common.network;

public class Method {
//    public enum Methods {
//        PING,
//        QUERY,
//        ADD,
//        CHANGE,
//        MONITOR,
//        CANCEL,
//        EXTEND
//    }

    public static String METHOD = "method";
    public static String PAYLOAD = "payload";

    public static int PING = 0;
    public static int QUERY = 1;
    public static int ADD = 2;
    public static int CHANGE = 3;
    public static int MONITOR = 4;
    public static int CANCEL = 5;
    public static int EXTEND = 6;

    public enum Query {
        FACILITY
    }

    public enum Add {
        START,
        END,
        FACILITY
    }

    public enum Change {
        UUID,
        OFFSET
    }

    public enum Monitor {
        FACILITY,
        INTERVAL,
        CLIENTADDR,
        CLIENTPORT
    }

    public enum Extend {
        UUID,
        EXTEND
    }

    public enum Cancel {
        UUID
    }

}
