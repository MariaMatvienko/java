package com.mariamatvienko.olx_lab;

public class AdvertisementInfo {

    public String id;

    public String url;

    public String name;

    public String price;

    public String publish;

    public String description;

    public AdvertisementInfo(String id, String url, String name, String price, String publish, String description) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.price = price;
        this.publish = publish;
        this.description = description;
    }
}
