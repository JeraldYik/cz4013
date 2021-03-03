package server;

import common.network.MethodNotFoundException;
import common.network.RawMessage;
import common.network.Transport;

public class Handler {
    public static void handle(Transport server, RawMessage req) {
        switch (req.obj.get("method")) {
            case "ping":
                System.out.println("Message received from client: ");
                System.out.println(req.obj);

                String reply = "From server: " + req.obj.get("message");
                server.send(req.address, common.Util.putInHashMapPacket("ping", reply));
                System.out.println("Message sent to client.");
                break;
            default:
                throw new MethodNotFoundException("Method not handled");
        }
    }
}
