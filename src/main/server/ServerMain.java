package main.server;

import main.common.facility.Facilities;
import main.common.network.Transport;

import java.net.*;


public class ServerMain {
    public static void main(String[] args) throws SocketException {
        String serverHost = "127.0.0.1";
        int port = 49155;
        boolean atMostOnce = true;

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        Facilities facilities = new Facilities();

        if (atMostOnce) {
            System.out.print("Current server mode: At-Most-Once");
            try {
                AtMostOnceHandler handler = new AtMostOnceHandler();
                while (true) {
                    DatagramPacket p = server.receive();
                    if (p.getLength() != 0) {
                        handler.handle(server, facilities, p);
                    } else {
                        System.out.println("Packet received from client is null");
                    }
                }
            } catch (Exception e) {
                System.out.println("Server.Main - " + e.getClass().toString() + ": " + e.getMessage());
            }
        }
        else {
            System.out.print("Current server mode: At-Least-Once");
            try {
                DefaultHandler handler = new DefaultHandler();
                while (true) {
                    DatagramPacket p = server.receive();
                    if (p.getLength() != 0) {
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
}
