package main.server;

import main.common.facility.Facilities;
import main.common.network.Transport;

import java.io.IOException;
import java.net.*;


public class ServerMain {
    public static void main(String[] args) throws SocketException {
        String serverHost  = "127.0.0.1";
        int port = 49152;
        boolean atMostOnce = false;

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        Facilities facilities = new Facilities();

        DefaultHandler handler = new DefaultHandler();

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

        try {
            while (true) {

                DatagramPacket p = server.receive();

                if(p.getLength() != 0) {
                    handler.handle(server, facilities, p);
                } else {
                    System.out.println("Packet received from client is null");
                }
            }
        } catch(RuntimeException e) {
            System.out.println("Server.Main - Runtime Exception! " + e.getMessage());
        } catch(IOException e) {
            System.out.println("Server.Main - IO Exception! " + e.getMessage());
        }
    }

}
