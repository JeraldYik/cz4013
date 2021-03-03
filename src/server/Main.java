package server;

import common.network.RawMessage;
import common.network.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws SocketException {
        String serverHost  = "127.0.0.1";
        int port = 49152;

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        /** maybe there's no need for rmi architecture
        try {
            Servant servant = new Servant();
//            Registry r = LocateRegistry.createRegistry(port);

            RMIRegistry registry = RMIRegistry.getInstance();
            String rmiName = "rmi://" + serverHost + ":" + port + "/City";
//            Naming.rebind(rmiName, servant);
            registry.rebind(rmiName, servant);

        } catch (RemoteException e) {
            System.out.println("Remote Exception! " + e.getMessage());
        }
//        catch (MalformedURLException e) {
//            System.out.println("MalformedURL Exception! " + e.getMessage());
//        }
        catch (Exception e) {
            System.out.println("Exception! " + e.getMessage());
        }
         **/

        /** TODO:
         *  Create a handler Class to handle different request method
         */
        try {
            while (true) {
                RawMessage req = server.receive();
                Handler.handle(server, req);
            }
        } catch(RuntimeException e) {
            System.out.println("Runtime Exception! " + e.getMessage());
        }
    }

}
