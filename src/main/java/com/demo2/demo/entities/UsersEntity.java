package com.demo2.demo.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users", schema = "ais")
public class UsersEntity implements Serializable {
    private int uid;
    private String username;
    private String password;
    private String lastLng;
    private String latLat;

    @Id
    @Column(name = "uid")
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "last_lng")
    public String getLastLng() {
        return lastLng;
    }

    public void setLastLng(String lastLng) {
        this.lastLng = lastLng;
    }

    @Basic
    @Column(name = "lat_lat")
    public String getLatLat() {
        return latLat;
    }

    public void setLatLat(String latLat) {
        this.latLat = latLat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersEntity that = (UsersEntity) o;

        if (uid != that.uid) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (lastLng != null ? !lastLng.equals(that.lastLng) : that.lastLng != null) return false;
        if (latLat != null ? !latLat.equals(that.latLat) : that.latLat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (lastLng != null ? lastLng.hashCode() : 0);
        result = 31 * result + (latLat != null ? latLat.hashCode() : 0);
        return result;
    }
}
