package com.demo2.demo.entities;

import java.util.Collection;
import java.util.List;

public class Area {
    private String lng1;
    private String lng2;
    private String lat1;
    private String lat2;
    private Collection<DynamicEntity> collection;
    private Period period;
    public Area() {
    }

    public Area(Collection<DynamicEntity> collection) {
        this.collection = collection;
    }

    public Area(String lng1, String lng2, String lat1, String lat2) {
        this.lng1 = lng1;
        this.lng2 = lng2;
        this.lat1 = lat1;
        this.lat2 = lat2;
    }

    @Override
    public String toString() {
        return "Area{" +
                "lng1='" + lng1 + '\'' +
                ", lng2='" + lng2 + '\'' +
                ", lat1='" + lat1 + '\'' +
                ", lat2='" + lat2 + '\'' +
                ", collection=" + collection +
                '}';
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getLng1() {
        return lng1;
    }

    public void setLng1(String lng1) {
        this.lng1 = lng1;
    }

    public String getLng2() {
        return lng2;
    }

    public void setLng2(String lng2) {
        this.lng2 = lng2;
    }

    public String getLat1() {
        return lat1;
    }

    public void setLat1(String lat1) {
        this.lat1 = lat1;
    }

    public String getLat2() {
        return lat2;
    }

    public void setLat2(String lat2) {
        this.lat2 = lat2;
    }

    public Collection<DynamicEntity> getCollection() {
        return collection;
    }

    public void setCollection(Collection<DynamicEntity> collection) {
        this.collection = collection;
    }
}
