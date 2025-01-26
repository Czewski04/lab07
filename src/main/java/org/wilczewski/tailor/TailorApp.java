package org.wilczewski.tailor;

import interfaces.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.wilczewski.controlcenter.ControlCenterController;
import org.wilczewski.controlcenter.ControlCenterService;
import org.wilczewski.myrmiinterface.ISettingName;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TailorApp extends Application implements ITailor {
    IControlCenter controlCenter;
    IEnvironment environment;
    HashMap<String, IRetensionBasin> retensionBasinStringHashMap = new HashMap<>();
    HashMap<String, IRiverSection> riverSectionStringHashMap = new HashMap<>();

    @Override
    public boolean register(Remote remote, String s) throws RemoteException {
        if(remote instanceof IRetensionBasin && remote instanceof ISettingName){
            s = "RetentionBasin" + (retensionBasinStringHashMap.size() + 1);
            ((ISettingName) remote).setName(s);
            retensionBasinStringHashMap.put(s, (IRetensionBasin) remote);
            controlCenter.assignRetensionBasin((IRetensionBasin)remote, s);
        }
        else if(remote instanceof IRiverSection && remote instanceof ISettingName){
            s = "RiverSection" + (riverSectionStringHashMap.size() + 1);
            ((ISettingName) remote).setName(s);
            riverSectionStringHashMap.put(s, (IRiverSection) remote);
            environment.assignRiverSection((IRiverSection)remote, s);
        }
        else if(remote instanceof IEnvironment){
            environment = (IEnvironment)remote;
        }
        else if(remote instanceof IControlCenter){
            controlCenter = (IControlCenter)remote;
        }

        return true;
    }

    @Override
    public boolean unregister(Remote remote) throws RemoteException {
        return false;
    }

    public void connect() throws RemoteException {
        riverSectionStringHashMap.get("RiverSection1").assignRetensionBasin(retensionBasinStringHashMap.get("RetentionBasin1"), "RetentionBasin1");
        retensionBasinStringHashMap.get("RetentionBasin1").assignRiverSection(riverSectionStringHashMap.get("RiverSection2"), "RiverSection2");
        riverSectionStringHashMap.get("RiverSection2").assignRetensionBasin(retensionBasinStringHashMap.get("RetentionBasin2"), "RetentionBasin2");
        riverSectionStringHashMap.get("RiverSection3").assignRetensionBasin(retensionBasinStringHashMap.get("RetentionBasin2"), "RetentionBasin2");
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TailorAppView.fxml"));
        Parent root = loader.load();

        TailorApp tailorApp = new TailorApp();
        try{
            ITailor iTailor = (ITailor) UnicastRemoteObject.exportObject(tailorApp, 0);
            Registry registry = LocateRegistry.createRegistry(2000);
            registry.rebind("Tailor", iTailor);
        }catch(RemoteException e){
            e.printStackTrace();
        }

        TailorController controller = loader.getController();
        controller.setTailorApp(tailorApp);

        stage.setTitle("Tailor");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
