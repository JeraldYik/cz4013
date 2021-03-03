package client;

import common.network.RawMessage;
import common.network.Transport;

import java.net.SocketAddress;

public class Client {
    private final Transport transport;
    private final SocketAddress serverAddr;

    public Client(Transport transport, SocketAddress serverAddr) {
        this.transport = transport;
        this.serverAddr = serverAddr;
    }

    public void sendMessageToServer() {
        try {
            System.out.print("Your message: ");
            String testmsg = Util.readLine();
            this.transport.send(serverAddr, common.Util.putInHashMapPacket("ping", testmsg));
            System.out.println("Message sent to server.");

            /** Add timeout here **/

            RawMessage res = this.transport.receive();
            if (res.obj.get("method").equals("ping")) {
                System.out.println("Message received from server: ");
                System.out.println(res.obj);
            } else {
                System.out.println("unknown method");
            }


        } catch(RuntimeException e) {
            System.out.println("Runtime Exception! " + e.getMessage());
        }
    }

    public static void testRMI(String serverHost, int serverPort) {
        /** TODO:
         *  Use socket programming
         *  maybe there's no need for rmi architecture
         */
//         try {
//             String rmiName = "rmi://" + serverHost + ":" + serverPort + "/City";
//             RMIRegistry registry = RMIRegistry.getInstance();
////             IRemote cityServer = (IRemote) Naming.lookup(rmiName);
//             IRemote cityServer = (IRemote) registry.lookup(rmiName);
//
//         int pop = cityServer.getPopulation("Toronto");
//            System.out.println("pop: " + pop);
//         } catch (Exception e) {
//            System.out.println("Exception! " + e.getMessage());
//         }
    }
}
