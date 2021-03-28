package main.common.facility;

public class NodeInformation {
    String address;
    int port;
    Facilities.Types type;

    public NodeInformation(String addr, int port, Facilities.Types t) {
        this.address = addr;
        this.port = port;
        this.type = t;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

}
