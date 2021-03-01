package common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRMIRegistry {
    //    Used by a server to register the identifier of a remote object by name.
    void rebind (String name, IRemote obj) throws RemoteException;
    //    Used alternatively by a server to register a remote object by name, but if the name is already bound to a remote object reference, an exception is thrown.
    void bind (String name, IRemote obj) throws RemoteException;
    //    This method removes a binding.
    void unbind (String name, IRemote obj) throws RemoteException;
    //    For clients to look up a remote object by name. A remote object reference is returned.
    Remote lookup(String name) throws RemoteException;
    //    This method returns an array of Strings containing the names bound in the registry.
    String [] list() throws RemoteException;
}
