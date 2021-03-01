package server;

import common.rmi.Servant;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) {
        String serverHost  = "0.0.0.0";
        int port = 49152;

        try {
            Servant servant = new Servant();
            LocateRegistry.createRegistry(port);
            String rmiName = "rmi://" + serverHost + ":" + port + "/City";
            Naming.rebind(rmiName, servant);
            System.out.println("Listening on udp://" + serverHost + ":" + port);
        } catch (RemoteException e) {
            System.out.println("Remote Exception! " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("MalformedURL Exception! " + e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Exception! " + e.getMessage());
        }
    }

}
