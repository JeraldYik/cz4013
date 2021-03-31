package main.common.network;

/**
 * The type Method.
 */
public class Method {

    /**
     * The constant PING.
     */
    public static int PING = 0;
    /**
     * The constant QUERY.
     */
    public static int QUERY = 1;
    /**
     * The constant ADD.
     */
    public static int ADD = 2;
    /**
     * The constant CHANGE.
     */
    public static int CHANGE = 3;
    /**
     * The constant MONITOR.
     */
    public static int MONITOR = 4;
    /**
     * The constant CANCEL.
     */
    public static int CANCEL = 5;
    /**
     * The constant EXTEND.
     */
    public static int EXTEND = 6;

    /**
     * The constant DELIMITER.
     */
    public static String DELIMITER = "#";

    /**
     * The enum Query.
     */
    public enum Query {
        /**
         * Facility query.
         */
        FACILITY
    }

    /**
     * The enum Add.
     */
    public enum Add {
        /**
         * Startday add.
         */
        STARTDAY,
        /**
         * Starthour add.
         */
        STARTHOUR,
        /**
         * Startmin add.
         */
        STARTMIN,
        /**
         * Endday add.
         */
        ENDDAY,
        /**
         * Endhour add.
         */
        ENDHOUR,
        /**
         * Endmin add.
         */
        ENDMIN,
        /**
         * Facility add.
         */
        FACILITY
    }

    /**
     * The enum Change.
     */
    public enum Change {
        /**
         * Uuid change.
         */
        UUID,
        /**
         * Offset change.
         */
        OFFSET
    }

    /**
     * The enum Monitor.
     */
    public enum Monitor {
        /**
         * Facility monitor.
         */
        FACILITY,
        /**
         * Interval monitor.
         */
        INTERVAL
    }

    /**
     * The enum Extend.
     */
    public enum Extend {
        /**
         * Uuid extend.
         */
        UUID,
        /**
         * Extend extend.
         */
        EXTEND
    }

    /**
     * The enum Cancel.
     */
    public enum Cancel {
        /**
         * Uuid cancel.
         */
        UUID
    }

    /**
     * The enum Ping.
     */
    public enum Ping {
        /**
         * Ping ping.
         */
        PING,
    }

}
