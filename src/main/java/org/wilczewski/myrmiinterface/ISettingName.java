package org.wilczewski.myrmiinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISettingName extends Remote {
    void setName(String name) throws RemoteException;
}
