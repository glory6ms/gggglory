package com.demo2.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "lnglat", schema = "ais")
public class LnglatEntity {
    private int id;
    private Double sn;
    private Double lng;
    private Double lat;
    private Double height;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "sn", nullable = true)
    public Double getSn() {
        return sn;
    }

    public void setSn(Double sn) {
        this.sn = sn;
    }

    @Basic
    @Column(name = "lng", nullable = true, precision = 0)
    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Basic
    @Column(name = "lat", nullable = true, precision = 0)
    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Basic
    @Column(name = "height", nullable = true, precision = 0)
    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LnglatEntity that = (LnglatEntity) o;
        return id == that.id &&
                Objects.equals(sn, that.sn) &&
                Objects.equals(lng, that.lng) &&
                Objects.equals(lat, that.lat) &&
                Objects.equals(height, that.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sn, lng, lat, height);
    }
}
