package server;

import common.network.RawMessage;
import common.network.Transport;
import common.rmi.Servant;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) throws SocketException {
        String serverHost  = "127.0.0.1";
        int port = 49152;

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        try {
            Servant servant = new Servant();
            LocateRegistry.createRegistry(port);
//            RMIRegistry registry = new RMIRegistry();
            String rmiName = "rmi://" + serverHost + ":" + port + "/City";
            Naming.rebind(rmiName, servant);
//            registry.rebind(rmiName, servant);

        } catch (RemoteException e) {
            System.out.println("Remote Exception! " + e.getMessage());
        }
//        catch (MalformedURLException e) {
//            System.out.println("MalformedURL Exception! " + e.getMessage());
//        }
        catch (Exception e) {
            System.out.println("Exception! " + e.getMessage());
        }

        /** TODO:
         *  Create a handler Class to handle different request method
         */
        try {
            while (true) {
                RawMessage req = server.receive();
                String msg = new String(req.buffer, 0, req.packet.getLength());
                System.out.println("Message received: " + msg);

                String reply = "Message received: " + msg;
                server.send(new InetSocketAddress(req.packet.getAddress(), req.packet.getPort()), reply);
                System.out.println("Message sent to client.");
//                byte[] rcvBuffer = new byte[8192]; // use CORBA Data Representation
//                DatagramPacket packetFromClient = new DatagramPacket(rcvBuffer, rcvBuffer.length);
//                socket.receive(packetFromClient);
//                System.out.println("Length of response from client: " + packetFromClient.getLength() + " bytes.");
//                String msg = new String(rcvBuffer, 0, packetFromClient.getLength());
//                System.out.println("Message from client: " + msg);
//
//                String reply = "Message received: " + msg;
//                byte[] sendBuffer = reply.getBytes();
//                InetAddress clientAddress = packetFromClient.getAddress();
//                int clientPort = packetFromClient.getPort();
//                DatagramPacket responseToClient = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
//                socket.send(responseToClient);
//                System.out.println("Message sent to client");
            }
        } catch(RuntimeException e) {
            System.out.println("Runtime Exception! " + e.getMessage());
        }
    }

}
