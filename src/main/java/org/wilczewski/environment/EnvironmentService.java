package org.wilczewski.environment;

import interfaces.IEnvironment;
import interfaces.IRiverSection;
import interfaces.ITailor;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EnvironmentService extends UnicastRemoteObject implements IEnvironment {
    //private ConcurrentHashMap<Integer, String> riverSectionsMap;
    private ConcurrentHashMap<String, IRiverSection> iRiverSectionsMap;
    private EnvironmentController controller;

    public EnvironmentService(EnvironmentController controller) throws NotBoundException, RemoteException {
        this.controller = controller;
        iRiverSectionsMap = new ConcurrentHashMap<>();
        registerInTailor();
    }

    private void registerInTailor() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor iTailor = (ITailor) registry.lookup("Tailor");
        iTailor.register(this, "Environment");
    }

//    public void configuration() throws IOException {
//
//    }

    public void run(){
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(()->controller.showRivers(iRiverSectionsMap));
            }
        });
        thread.start();
    }

    @Override
    public void assignRiverSection(IRiverSection iRiverSection, String s) throws RemoteException {
        iRiverSectionsMap.put(s,iRiverSection);
    }

    public void setRainfall(String name, int rainfall) throws IOException {
        iRiverSectionsMap.get(name).setRainfall(rainfall);
    }



}
