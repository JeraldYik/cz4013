package main.common.facility;

import java.net.InetSocketAddress;

/**
 * The type Node information.
 */
public class NodeInformation {
    /**
     * The Addr.
     */
    InetSocketAddress addr;
    /**
     * The Type.
     */
    Facilities.Types type;

    /**
     * Gets addr.
     *
     * @return the addr
     */
    public InetSocketAddress getAddr() {
        return this.addr;
    }

    /**
     * Instantiates a new Node information.
     *
     * @param addr the addr
     * @param t    the t
     */
    public NodeInformation(InetSocketAddress addr, Facilities.Types t) {
        this.addr = addr;
        this.type = t;
    }
}
