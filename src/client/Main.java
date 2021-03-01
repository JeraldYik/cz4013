package client;

import common.rmi.RemoteInterface;

import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws SocketException {
        String clientHost = "0.0.0.0";
        String serverHost = "127.0.0.1";
        int clientPort = 49153;
        int serverPort = 49152;
//        DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientHost, clientPort));
//
//        String test = "Hello World";
//
//        try {
//
//        } catch (Exception e) {
//            System.out.println("Exception! " + e.getMessage());
//        }
        try {
            String rmiName = "rmi://" + serverHost + ":" + serverPort + "/City";
            RemoteInterface cityServer = (RemoteInterface) Naming.lookup(rmiName);
            int pop = cityServer.getPopulation("Toronto");
            System.out.println("pop: " + pop);
        } catch (RemoteException e) {
            System.out.println("Remote Exception! " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception! " + e.getMessage());
        }
    }
}
