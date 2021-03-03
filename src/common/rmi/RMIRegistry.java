package common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public final class RMIRegistry implements IRMIRegistry{
    private Map<String, IRemote> mapping;
    private static RMIRegistry instance = null;

    private RMIRegistry() {
        super();
        this.mapping = new HashMap<>();
    }

    public static RMIRegistry getInstance() {
        System.out.println("in get Instance");
        System.out.println(instance);
        if (instance == null) {
            instance = new RMIRegistry();
        }
        return instance;
    }

    public void rebind (String name, IRemote obj) throws RemoteException {
        System.out.println(instance);
        this.mapping.put(name, obj);
        System.out.println(this.mapping);
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
        System.out.println(instance);
        System.out.println(this.mapping);
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
