package client;

import common.network.RawMessage;
import common.network.Transport;
import common.rmi.IRemote;

import java.net.SocketAddress;
import java.rmi.Naming;

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
            this.transport.send(serverAddr, testmsg);
            System.out.println("Message sent to server.");

            RawMessage res = this.transport.receive();
            String msg = new String(res.buffer, 0, res.packet.getLength());
            System.out.println("Message received: " + msg);

        } catch(RuntimeException e) {
            System.out.println("Runtime Exception! " + e.getMessage());
        }
    }

    public static void testRMI(String serverHost, int serverPort) {
        /** TODO:
         *  Use socket programming
         */
         try {
             String rmiName = "rmi://" + serverHost + ":" + serverPort + "/City";
             IRemote cityServer = (IRemote) Naming.lookup(rmiName);

             int pop = cityServer.getPopulation("Toronto");
                System.out.println("pop: " + pop);
             } catch (Exception e) {
                System.out.println("Exception! " + e.getMessage());
             }
        }
}
