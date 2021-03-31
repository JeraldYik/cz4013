package main.client;

import main.common.network.Transport;

import java.net.*;
import java.util.Scanner;

import static java.lang.Integer.parseInt;
import static main.client.Util.*;

public class ClientMain {
    public static void main(String[] args) throws SocketException {

        String clientAddr = "0.0.0.0";
        String serverAddr = "127.0.0.1";
        int serverPort = 49152;
        int clientPort = 49153;

        String MANUAL =
                "----------------------------------------------------------------\n" +
                "Please choose a service by typing [1-]:\n" +
                "1: Query Availability of a Facility\n"+
                "2: Add Booking to a Facility\n" +
                "3: Change Booking to a Facility\n" +
                "4: Monitor Availability of a Facility\n" +
                "5: (Idempotent) Cancel an active Booking\n" +
                "6: (Non-Idempotent) Extend an active Booking time in 30-minute block\n" +
                "7: View available test statements\n" +
                "8: Ping main.server\n" +
                "9: Print the manual\n" +
                "0: Stop the main.client\n";

        String TEST =
                "----------------------------------------------------------------\n" +
                "Please choose a test function by typing [1-]:\n" +
                "1: Send repeated ping requests with duplicate message IDs\n" +
                "2: Send repeated booking cancellation requests with duplicate message IDs (idempotent)\n" +
                "3: Send repeated booking extension requests with duplicate message IDs (non-idempotent)\n" +
                "4: Back to main menu\n" +
                "5: Print menu\n";


        try {
            String userAddr = readLine("Please enter preferred server IP address (enter '0' for default value): ");
            serverAddr = (userAddr.equals("0")) ? userAddr : "127.0.0.1";
            int userPort = safeReadInt("Please enter preferred server port number (enter '0' for default value): ");
            serverPort = (userPort != 0) ? userPort : 49152;

            userAddr = readLine("Please enter preferred client IP address (enter '0' for default value): ");
            if(parseInt(userAddr)!=0) clientAddr = userAddr;
            userPort = safeReadInt("Please enter preferred client port number (enter '0' for default value): ");
            if(userPort!=0) clientPort = userPort;
        } catch (Exception e) { System.out.println("Invalid values entered! Default values will be used."); }

        double failureProbability = safeReadDouble("Enter preferred server reply failure probability (0.0 - 1.0): ");
        while (failureProbability > 1.0) {
            failureProbability = safeReadDouble("Please enter a valid probability (0.0 - 1.0): ");
        }

        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientAddr, clientPort));
        Client client = new Client(new Transport(socket, 8192), new InetSocketAddress(serverAddr, serverPort), failureProbability); // use CORBA Data Representation

        System.out.println(
                "\nConfigured Server IP Address: " + serverAddr +
                "\nConfigured Server Port Number: " + serverPort +
                "\nConfigured Server failure probability rate: " + failureProbability
        );

        boolean terminate = false;
        System.out.print("\n" + MANUAL);
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
                    boolean quit = false;
                    System.out.println(TEST);
                    while(!quit) {
                        int testChoice = safeReadInt("Choose your function (Enter '5' for menu): ");
                        switch (testChoice) {
                            case 1:
                                client.sendDuplicatePingsToServer();
                                break;
                            case 2:
                                client.sendDuplicateCancelsToServer();
                            case 3:
                                client.sendDuplicateExtendsToServer();
                                break;
                            case 4:
                                quit = true;
                                break;
                            case 5:
                                System.out.println(TEST);
                                break;
                        }
                    }
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
