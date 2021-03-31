package main.server;

import main.common.facility.Facilities;
import main.common.network.Transport;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import static main.client.Util.safeReadInt;


/**
 * The type Server main.
 */
public class ServerMain {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws SocketException the socket exception
     */
    public static void main(String[] args) throws SocketException {
        String serverHost = "127.0.0.1";
        int port = 49152;
        boolean atMostOnce = false;

        System.out.print(
                "0: At-Least-Once Server\n" +
                        "1: At-Most-Once Server\n");
        int serverChoice = safeReadInt("Server choice: ");
        switch (serverChoice) {
            case 0:
                break;
            case 1:
                atMostOnce = true;
                break;
        }

        Transport server = new Transport(new DatagramSocket(new InetSocketAddress(serverHost, port)), 8192); // use CORBA Data Representation
        System.out.println("Listening on udp://" + serverHost + ":" + port);

        Facilities facilities = new Facilities();
        Handler handler = new Handler();

        if (atMostOnce) {
            System.out.print("Current server mode: At-Most-Once");
        } else {
            System.out.print("Current server mode: At-Least-Once");
        }

        try {
            while (true) {
                DatagramPacket p = server.receive();
                if (p.getLength() != 0) {
                    handler.handle(server, facilities, p, atMostOnce);
                } else {
                    System.out.println("Packet received from client is null");
                }
            }
        } catch (Exception e) {
            System.out.println("Server.Main - " + e.getClass().toString() + ": " + e.getMessage());
        }
    }
}
