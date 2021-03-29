package main.server;

import main.common.facility.Facilities;
import main.common.network.RawMessage;
import main.common.network.Transport;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;


public class Main {
    public static void main(String[] args) throws SocketException {
        String serverHost  = "127.0.0.1";
        int port = 49152;

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        Facilities facilities = new Facilities();

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
                    Handler.handle(server, facilities, p);
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
