package common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
    int getPopulation(String cityName) throws RemoteException;
    int getTemperature(String cityName) throws RemoteException;
}
