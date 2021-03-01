package common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class RMIRegistry implements IRMIRegistry{
    private Map<String, IRemote> mapping;

    public RMIRegistry() {
        super();
        this.mapping = new HashMap<String, IRemote>();
    }

    public void rebind (String name, IRemote obj) throws RemoteException {
        this.mapping.put(name, obj);
    }

    public void bind (String name, IRemote obj) throws RemoteException {
        if (this.mapping.containsKey(name)) {
            throw new RemoteException("Name " + name + " already bounded to a remote object reference.");
        }
        this.mapping.put(name, obj);
    }

    public void unbind (String name, IRemote obj) throws RemoteException {
        if (!this.mapping.containsKey(name)) {
            throw new RemoteException("Name " + name + " not bounded to a remote object reference yet.");
        }
        this.mapping.remove(name);
    }

    public Remote lookup(String name) throws RemoteException {
        if (!this.mapping.containsKey(name)) {
            throw new RemoteException("Name " + name + " not found in Registry.");
        }
        return this.mapping.get(name);
    }

    public String [] list() throws RemoteException {
        if (this.mapping.isEmpty()) {
            throw new RemoteException("Registry is empty.");
        }
        return this.mapping.keySet().toArray(new String[0]);
    }
}
