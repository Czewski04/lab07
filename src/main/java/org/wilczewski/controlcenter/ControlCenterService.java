package org.wilczewski.controlcenter;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;
import javafx.application.Platform;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ControlCenterService extends UnicastRemoteObject implements IControlCenter {
    private ConcurrentHashMap<String, RetentionBasinMapItem> retentionBasinsMap;
    private ConcurrentHashMap<String, IRetensionBasin> iRetentionBasinsMap;
    private ControlCenterController controller;

    public ControlCenterService(ControlCenterController controller) throws RemoteException, NotBoundException {
        this.controller = controller;
        retentionBasinsMap = new ConcurrentHashMap<>();
        iRetentionBasinsMap = new ConcurrentHashMap<>();
        registerInTailor();
    }

//    public void configuration() throws IOException, NotBoundException {
//        registerInTailor();
//    }

    private void registerInTailor() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor iTailor = (ITailor) registry.lookup("Tailor");
        iTailor.register(this, "ControlCenter");
    }

    public void run() {
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                iRetentionBasinsMap.forEach((mapItem, iRetentionBasin) -> {
                    try {
                        retentionBasinsMap.get(mapItem).setFillingPercentage(((double)iRetentionBasin.getFillingPercentage())/100);
                        retentionBasinsMap.get(mapItem).setWaterDischargeValve(iRetentionBasin.getWaterDischarge());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(()->controller.showBasins(retentionBasinsMap));
            }
        });
        thread.start();
    }


    @Override
    public void assignRetensionBasin(IRetensionBasin iRetentionBasin, String s) throws RemoteException {
        iRetentionBasinsMap.put(s, iRetentionBasin);
        retentionBasinsMap.put(s, new RetentionBasinMapItem());
        retentionBasinsMap.get(s).setFillingPercentage(((double)iRetentionBasin.getFillingPercentage())/100);
        retentionBasinsMap.get(s).setWaterDischargeValve(iRetentionBasin.getWaterDischarge());
    }

    public void setRetentionBasinWaterDischarge(String name, int waterDischarge) throws IOException {
        iRetentionBasinsMap.get(name).setWaterDischarge(waterDischarge);
        retentionBasinsMap.get(name).setWaterDischargeValve(waterDischarge);
    }

}
