package com.byteshaft.carecare.gettersetter;

import java.util.ArrayList;

public class AutoMechanicItems {

    private String serviceName;
    private String categoryName;
    private int serviceId;
    private int categoryId;
    private String servicePrice;
    private ArrayList<AutoMechanicSubItem> subItemsArrayList;

    public ArrayList<AutoMechanicSubItem> getSubItemsArrayList() {
        return subItemsArrayList;
    }

    public void setSubItemsArrayList(ArrayList<AutoMechanicSubItem> subItemsArrayList) {
        this.subItemsArrayList = subItemsArrayList;
    }


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }


    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }


    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }
}
