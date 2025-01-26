package org.wilczewski.retentionbasin;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RetentionBasinService extends UnicastRemoteObject implements IRetensionBasin, ISettingName {
    private int maxVolume;
    private int volume;
    private int waterDischarge;
    private int realWaterInflow;
    private double fillingPercentage;
    private ConcurrentHashMap<String, Integer> inRiverSectionsWaterInflow;
    private IRiverSection iOutRiverSection;
    private RetentionBasinController controller;
    private String name;

    public RetentionBasinService(RetentionBasinController controller) throws RemoteException {
        this.controller = controller;
        this.inRiverSectionsWaterInflow = new ConcurrentHashMap<>();
    }

    public void configuration(int volume) throws IOException, InterruptedException, NotBoundException {
        this.maxVolume = volume;
        registerInTailor();
    }

    private void registerInTailor() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 2000);
        ITailor iTailor = (ITailor) registry.lookup("Tailor");
        iTailor.register(this, "");
    }

    public void run() throws IOException, InterruptedException {
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    if(iOutRiverSection != null)
                        iOutRiverSection.setRealDischarge(waterDischarge);
                } catch (InterruptedException | IOException | NullPointerException e) {
                    throw new RuntimeException(e);
                }
                calculateAmountOfWater();
                updateFillingPercentageBar();
                updateFlowDisplay();
            }
        });
        thread.start();
    }

    public void calculateAmountOfWater(){
        realWaterInflow = 0;
        inRiverSectionsWaterInflow.forEach((name, water) -> {
            realWaterInflow += water;
        });
        volume += realWaterInflow - waterDischarge;
        if(volume<0) volume = waterDischarge = 0;
        fillingPercentage = (double)volume/(double)maxVolume;
        if(fillingPercentage >= 1) waterDischarge = realWaterInflow;
    }

    public void updateFlowDisplay(){
        Platform.runLater(()-> controller.updateFlow(realWaterInflow, waterDischarge));
    }

    public void updateFillingPercentageBar(){
        Platform.runLater(()-> controller.updateVolume(fillingPercentage));
    }

    @Override
    public int getWaterDischarge() throws RemoteException {
        return waterDischarge;
    }

    @Override
    public long getFillingPercentage() throws RemoteException {
        fillingPercentage = (double)volume/(double)maxVolume;
        return (long) (fillingPercentage*100);
    }

    @Override
    public void setWaterDischarge(int waterDischarge) throws RemoteException {
        this.waterDischarge = waterDischarge;
    }

    @Override
    public void setWaterInflow(int waterInflow, String name) throws RemoteException {
        inRiverSectionsWaterInflow.put(name, waterInflow);
    }

    @Override
    public void assignRiverSection(IRiverSection iRiverSection, String s) throws RemoteException {
        iOutRiverSection = iRiverSection;
    }

    @Override
    public void setName(String name) throws RemoteException {
        this.name = name;
    }
}
