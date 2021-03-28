package main.common.network;

public class Constants {
    // Timeouts and failure rate
    public static final double DEFAULT_FAIL_RATE = 0.0;
    public static final int DEFAULT_TIMEOUT = 1000;
    public static final int DEFAULT_NO_TIMEOUT = 0;
    public static final int DEFAULT_MAX_TIMEOUT = 0;

    // Acknowledgements
    public static final int ACK = 1;
    public static final int NACK = 0;

    // Invocation Semantics
    public static final int NORMAL_INVO = 0;
    public static final int ALO_INVO = 1;
    public static final int AMO_INVO = 2;

}
