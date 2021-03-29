package main.common.facility;

import java.net.InetSocketAddress;

public class NodeInformation {
    InetSocketAddress addr;
    Facilities.Types type;

    public InetSocketAddress getInetSocketAddress() {
        return this.addr;
    }

    public NodeInformation(InetSocketAddress addr, Facilities.Types t) {
        this.addr = addr;
        this.type = t;
    }
}
