package main.server;

import main.common.facility.Facilities;
import main.common.network.Transport;

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

        try {
            while (true) {

                DatagramPacket p = server.receive();

                if(p.getLength() != 0) {
                    handler.handle(server, facilities, p);
                } else {
                    System.out.println("Packet received from client is null");
                }
            }
        } catch (Exception e) {
            System.out.println("Server.Main - " + e.getClass().toString() + ": " + e.getMessage());
        }
    }

}
