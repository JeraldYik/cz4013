package main.common.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Servant extends UnicastRemoteObject implements IRemote {
    public Servant() throws RemoteException {
        super();
    }
    public int getPopulation(String cityName) throws RemoteException {
        if (cityName.equals("Toronto")) { return 10; }
        else if (cityName.equals("Ottawa")) { return 2; }
        else { return 100; }
    }

    public int getTemperature(String cityName) throws RemoteException {
        if (cityName.equals("Toronto")) { return 20; }
        else if (cityName.equals("Ottawa")) { return 30; }
        else { return 10; }
    }
}
