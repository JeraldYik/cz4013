package main.server.main;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import main.server.facilities.*;
import main.server.services.*;
import main.server.socket.*;

import javax.security.auth.callback.CallbackHandler;

public class ServerApplication {

    private static Server server;
    private static Facilities facilities;
    private static CallbackHandler callbackHandler;
    private static InetAddress addr;

    private static Socket socket;
    private static int port;

    public static void main(String[] args){
        Console console =  new Console(new Scanner(System.in));

        try{
            System.out.println("Starting Booking Server");

            String addrInput = console.askForString("Enter the IP address of the server: ");
            addr = InetAddress.getByName(addrInput);

            port = console.askForInteger("Enter port number: ");
            socket = new NormalSocket(new DatagramSocket(port, addr));

            int serverType = console.askForInteger(1, 2, "Enter server invocation semantics: \n1) At-least-once \n2)At-most-once");
            if (serverType == 1){
                server = new Server(socket);
            } else if (serverType == 2){
                server = new AtMostOnceServer(socket);
            }

            int socketType = console.askForInteger(1, 3, "Enter socket type: \n1) Normal\n2) Sending Loss\n3) Corrupted");
            if (socketType == 2) {
                double p = 1 - console.askForDouble(0.0, 1.0, "Enter packet loss probability: ");
//                server.useSendingLossSocket(p);
            } else if (socketType == 3){
                double p = 1 - console.askForDouble(0.0, 1.0, "Enter packet loss probability: ");
//                server.useCorruptedSocket(p);
            }


            callbackHandler = new CallbackHandlerClass(socket) {
            }
        }
    }
}
