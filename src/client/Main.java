package client;

import common.network.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws SocketException {
        String clientHost = "0.0.0.0";
        String serverHost = "127.0.0.1";
        int serverPort = 49152;
        int clientPort = 49153;

        String MANUAL = "----------------------------------------------------------------\n" +
                "Please choose a service by typing [1-]:\n" +
                "1: Send a message to server\n" +
                "2: Test RMI\n" +
                "9: Print the manual\n" +
                "0: Stop the client\n";

        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientHost, clientPort));
        Client client = new Client(new Transport(socket, 8192), new InetSocketAddress(serverHost, serverPort)); // use CORBA Data Representation

        boolean terminate = false;
        System.out.print(MANUAL);
        while (!terminate) {
            int userChoice = askUserChoice();
            switch (userChoice) {
                case 1:
                    client.sendMessageToServer();
                    break;
                case 2:
                    client.testRMI(serverHost, serverPort);
                    break;
                case 9:
                    System.out.print(MANUAL);
                    break;
                case 0:
                    terminate = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
        Util.closeReader();
        System.out.println("Stopping client...");
    }

    private static int askUserChoice() {
        System.out.print("\n----------------------------------------------------------------\n" +
                "Your choice = ");
        return Util.safeReadInt("Your choice = ");
    }
}
