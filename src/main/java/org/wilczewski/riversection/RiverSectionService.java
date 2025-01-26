package org.wilczewski.riversection;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import javafx.application.Platform;
import org.wilczewski.myrmiinterface.ISettingName;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.TimeUnit;

public class RiverSectionService extends UnicastRemoteObject implements IRiverSection, ISettingName {
    private float delay;
    private int waterOutflow;
    private int rainfall;
    private int realDischarge;
    private IRetensionBasin iOutRetentionBasin;
    RiverSectionController controller;
    private String name;

    public RiverSectionService(RiverSectionController controller) throws RemoteException {
        this.controller = controller;
    }

    public void configuration(float delay) throws IOException, NotBoundException {
        this.delay = delay;
        registerInTailor();
    }

    private void registerInTailor() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor iTailor = (ITailor) registry.lookup("Tailor");
        iTailor.register(this, "");
    }

    public void run() throws IOException {
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    calculateOutflow();
                    if(iOutRetentionBasin != null)
                        iOutRetentionBasin.setWaterInflow(waterOutflow, name);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public void calculateOutflow() {
        waterOutflow = 0;
        waterOutflow = realDischarge + rainfall;
        if(waterOutflow>0) Platform.runLater(() -> controller.showActiveRiverSign());
    }

    public void showRainfall(int rainfall) {
        Platform.runLater(()->controller.showRainfall(rainfall));
    }

    public void showWaterInflow(int waterInflow) {
        Platform.runLater(()->controller.showInflowWater(waterInflow));
    }

    @Override
    public void setRealDischarge(int realDischarge) throws RemoteException {
        try {
            TimeUnit.SECONDS.sleep((long) delay);
            this.realDischarge = realDischarge;
            showWaterInflow(realDischarge);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRainfall(int rainfall) throws RemoteException {
        this.rainfall = rainfall;
        showRainfall(rainfall);
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin iRetensionBasin, String s) throws RemoteException {
        iOutRetentionBasin = iRetensionBasin;
    }

    @Override
    public void setName(String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public String getName() throws RemoteException {
        return this.name;
    }
}
