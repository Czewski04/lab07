package org.wilczewski.controlcenter;

public class RetentionBasinMapItem {
    private double fillingPercentage;
    private int waterDischargeValve;

    public RetentionBasinMapItem() {
        this.fillingPercentage = 0.0;
        this.waterDischargeValve = 0;
    }

    public double getFillingPercentage() {
        return fillingPercentage;
    }

    public void setFillingPercentage(double fillingPercentage) {
        this.fillingPercentage = fillingPercentage;
    }

    public int getWaterDischargeValve() {
        return waterDischargeValve;
    }

    public void setWaterDischargeValve(int waterDischargeValve) {
        this.waterDischargeValve = waterDischargeValve;
    }
}

