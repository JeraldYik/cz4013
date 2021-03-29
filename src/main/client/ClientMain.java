package main.client;

import main.common.network.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static main.client.Util.safeReadInt;

public class ClientMain {
    public static void main(String[] args) throws SocketException {
        String clientAddr = "0.0.0.0";
        String serverAddr = "127.0.0.1";
        int serverPort = 49155;
        int clientPort = 49154;

        String MANUAL = "----------------------------------------------------------------\n" +
                "Please choose a service by typing [1-]:\n" +
                "1: Query Availability of a Facility\n"+
                "2: Add Booking to a Facility\n" +
                "3: Change Booking to a Facility\n" +
                "4: Monitor Availability of a Facility\n" +
                "5: (Idempotent) Cancel an active Booking\n" +
                "6: (Non-Idempotent) Extend an active Booking time in 30-minute block\n" +
                "7: Test RMI\n" +
                "8: Send a message to main.server\n" +
                "9: Print the manual\n" +
                "0: Stop the main.client\n";

        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientAddr, clientPort));
        Client client = new Client(new Transport(socket, 8192), new InetSocketAddress(serverAddr, serverPort)); // use CORBA Data Representation

        boolean terminate = false;
        System.out.print(MANUAL);
        while (!terminate) {
            int userChoice = safeReadInt("(MAIN MENU) Your choice of service ('9' for MANUAL): ");
            switch (userChoice) {
                case 1:
                    client.queryAvailability();
                    break;
                case 2:
                    client.addBooking();
                    break;
                case 3:
                    client.changeBooking();
                    break;
                case 4:
                    client.monitorAvailability();
                    break;
                case 5:
                    client.cancelBooking();
                    break;
                case 6:
                    client.extendBooking();
                    break;
                case 7:
                    client.testRMI(serverAddr, serverPort);
                    break;
                case 8:
                    client.sendMessageToServer();
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
        System.out.println("Stopping main.client...");
    }
}
