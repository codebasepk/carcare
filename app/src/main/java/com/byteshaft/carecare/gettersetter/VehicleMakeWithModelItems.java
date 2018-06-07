package com.byteshaft.carecare.gettersetter;

import java.util.ArrayList;
import java.util.HashMap;

public class VehicleMakeWithModelItems {

    private String vehicleModelName;
    private int vehicleModelId;
    private HashMap<Integer, ArrayList<CarCompanyItems>> hashMap;

    public String getVehicleModelName() {
        return vehicleModelName;
    }

    public void setVehicleModelName(String vehicleModelName) {
        this.vehicleModelName = vehicleModelName;
    }

    public int getVehicleModelId() {
        return vehicleModelId;
    }

    public void setVehicleModelId(int vehicleModelId) {
        this.vehicleModelId = vehicleModelId;
    }


    public  ArrayList<CarCompanyItems> getCompanyNames(Integer id) {
        return hashMap.get(id);
    }

    public void setHashMap(int id, ArrayList<CarCompanyItems> hashMap) {
        if (this.hashMap == null) {
            this.hashMap = new HashMap<>();
        }
        this.hashMap.put(id, hashMap);
    }

}
