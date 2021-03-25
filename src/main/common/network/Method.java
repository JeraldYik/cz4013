package main.common.network;

public class Method {
    public enum Methods {
        PING,
        QUERY,
        ADD,
        CHANGE,
        MONITOR,
        CANCEL,
        EXTEND
    }

    public static String METHOD = "method";
    public static String PAYLOAD = "payload";

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


}
