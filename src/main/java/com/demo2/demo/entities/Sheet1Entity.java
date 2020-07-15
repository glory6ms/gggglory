package com.demo2.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "sheet1", schema = "ais")
public class Sheet1Entity{
    private int id;
    private String name;
    private String lng;
    private String lat;
    private String modal1;
    private String modal2;


    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "lng", nullable = true, length = 255)
    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Basic
    @Column(name = "lat", nullable = true, length = 255)
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Basic
    @Column(name = "modal1", nullable = true, length = 255)
    public String getModal1() {
        return modal1;
    }

    public void setModal1(String modal1) {
        this.modal1 = modal1;
    }

    @Basic
    @Column(name = "modal2", nullable = true, length = 255)
    public String getModal2() {
        return modal2;
    }

    public void setModal2(String modal2) {
        this.modal2 = modal2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sheet1Entity that = (Sheet1Entity) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(lng, that.lng) &&
                Objects.equals(lat, that.lat) &&
                Objects.equals(modal1, that.modal1) &&
                Objects.equals(modal2, that.modal2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lng, lat, modal1, modal2);
    }
}
