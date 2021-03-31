package main.common.facility;

import java.net.InetSocketAddress;

public class NodeInformation {
    InetSocketAddress addr;
    Facilities.Types type;

    public InetSocketAddress getAddr() {
        return this.addr;
    }

    public NodeInformation(InetSocketAddress addr, Facilities.Types t) {
        this.addr = addr;
        this.type = t;
    }
}
