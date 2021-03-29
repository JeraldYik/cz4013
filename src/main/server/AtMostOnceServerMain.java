package main.server;

import main.common.facility.Facilities;
import main.common.network.Transport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class AtMostOnceServerMain {
    public static void main(String[] args) throws SocketException {
        String serverHost  = "127.0.0.1";
        int port = 49155;

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        Facilities facilities = new Facilities();

        AtMostOnceHandler handler = new AtMostOnceHandler();

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
