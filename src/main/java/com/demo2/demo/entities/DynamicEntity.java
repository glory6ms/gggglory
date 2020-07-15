package com.demo2.demo.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "dynamic", schema = "ais")
public class DynamicEntity {
    private Integer did;
    private Integer mmsi;
    private String shipName;
    private BigDecimal lng;
    private BigDecimal lat;
    private short turnRate;
    private BigDecimal landSpeed;
    private BigDecimal landCourse;
    private short shipCourse;
    private String shipState;
    private String precion;
    private short shipLength;
    private short shipWidth;
    private String shipType;
    private String aisType;
    private Date time;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    @Basic
    @Column(name = "MMSI", nullable = false)
    public Integer getMmsi() {
        return mmsi;
    }

    public void setMmsi(Integer mmsi) {
        this.mmsi = mmsi;
    }

    @Basic
    @Column(name = "Ship_Name", nullable = true, length = 40)
    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    @Basic
    @Column(name = "Lng", nullable = false,precision=16, scale=13)
    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    @Basic
    @Column(name = "Lat", nullable = false,precision=16, scale=13)
    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    @Basic
    @Column(name = "Turn_Rate",nullable = true)
    public short getTurnRate() {
        return turnRate;
    }

    public void setTurnRate(short turnRate) {
        this.turnRate = turnRate;
    }

    @Basic
    @Column(name = "Land_Speed", nullable = false,precision=4, scale=1)
    public BigDecimal getLandSpeed() {
        return landSpeed;
    }

    public void setLandSpeed(BigDecimal landSpeed) {
        this.landSpeed = landSpeed;
    }

    @Basic
    @Column(name = "Land_Course", nullable = false,precision=4, scale=1)
    public BigDecimal getLandCourse() {
        return landCourse;
    }

    public void setLandCourse(BigDecimal landCourse) {
        this.landCourse = landCourse;
    }

    @Basic
    @Column(name = "Ship_Course", nullable = false)
    public short getShipCourse() {
        return shipCourse;
    }

    public void setShipCourse(short shipCourse) {
        this.shipCourse = shipCourse;
    }

    @Basic
    @Column(name = "Ship_State", nullable = false, length = 40)
    public String getShipState() {
        return shipState;
    }

    public void setShipState(String shipState) {
        this.shipState = shipState;
    }

    @Basic
    @Column(name = "precion", nullable = false, length = 10)
    public String getPrecion() {
        return precion;
    }

    public void setPrecion(String precision) {
        this.precion = precion;
    }

    @Basic
    @Column(name = "Ship_Length", nullable = true)
    public short getShipLength() {
        return shipLength;
    }

    public void setShipLength(short shipLength) {
        this.shipLength = shipLength;
    }

    @Basic
    @Column(name = "Ship_Width", nullable = true)
    public short getShipWidth() {
        return shipWidth;
    }

    public void setShipWidth(short shipWidth) {
        this.shipWidth = shipWidth;
    }

    @Basic
    @Column(name = "Ship_Type", nullable = false, length = 20)
    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    @Basic
    @Column(name = "AISType", nullable = false, length = 10)
    public String getAisType() {
        return aisType;
    }

    public void setAisType(String aisType) {
        this.aisType = aisType;
    }

    @Basic
    @Column(name = "Time", nullable = false)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicEntity that = (DynamicEntity) o;

        if (mmsi != that.mmsi) return false;
        if (Double.compare(that.turnRate, turnRate) != 0) return false;
        if (shipCourse != that.shipCourse) return false;
        if (shipLength != that.shipLength) return false;
        if (shipWidth != that.shipWidth) return false;
        if (shipName != null ? !shipName.equals(that.shipName) : that.shipName != null) return false;
        if (lng != null ? !lng.equals(that.lng) : that.lng != null) return false;
        if (lat != null ? !lat.equals(that.lat) : that.lat != null) return false;
        if (landSpeed != null ? !landSpeed.equals(that.landSpeed) : that.landSpeed != null) return false;
        if (landCourse != null ? !landCourse.equals(that.landCourse) : that.landCourse != null) return false;
        if (shipState != null ? !shipState.equals(that.shipState) : that.shipState != null) return false;
        if (precion != null ? !precion.equals(that.precion) : that.precion != null) return false;
        if (shipType != null ? !shipType.equals(that.shipType) : that.shipType != null) return false;
        if (aisType != null ? !aisType.equals(that.aisType) : that.aisType != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mmsi;
        result = 31 * result + (shipName != null ? shipName.hashCode() : 0);
        result = 31 * result + (lng != null ? lng.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        temp = Double.doubleToLongBits(turnRate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (landSpeed != null ? landSpeed.hashCode() : 0);
        result = 31 * result + (landCourse != null ? landCourse.hashCode() : 0);
        result = 31 * result + (int) shipCourse;
        result = 31 * result + (shipState != null ? shipState.hashCode() : 0);
        result = 31 * result + (precion != null ? precion.hashCode() : 0);
        result = 31 * result + (int) shipLength;
        result = 31 * result + (int) shipWidth;
        result = 31 * result + (shipType != null ? shipType.hashCode() : 0);
        result = 31 * result + (aisType != null ? aisType.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }

    public DynamicEntity() {
    }

    public DynamicEntity(Integer mmsi, BigDecimal lng, BigDecimal lat) {
        this.mmsi = mmsi;
        this.lng = lng;
        this.lat = lat;
    }
    public DynamicEntity(BigDecimal lng, BigDecimal lat) {
        this.lng = lng;
        this.lat = lat;
    }
    public DynamicEntity(Integer mmsi, BigDecimal lng, BigDecimal lat, Date time) {
        this.mmsi = mmsi;
        this.lng = lng;
        this.lat = lat;
        this.time = time;
    }

    public DynamicEntity(BigDecimal lng, BigDecimal lat, String shipName) {
        this.lng = lng;
        this.lat = lat;
        this.shipName = shipName;
    }

    public DynamicEntity(Integer mmsi, BigDecimal lng, BigDecimal lat, BigDecimal landCourse, BigDecimal landSpeed, String shipState, Date time) {
        this.mmsi = mmsi;
        this.lng = lng;
        this.lat = lat;
        this.landCourse = landCourse;
        this.landSpeed = landSpeed;
        this.shipState = shipState;
        this.time = time;
    }

    @Override
    public String toString() {
        return "DynamicEntity{" +
                "mmsi=" + mmsi +
                ", shipName='" + shipName + '\'' +
                ", lng=" + lng +
                ", lat=" + lat +
                ", turnRate=" + turnRate +
                ", landSpeed=" + landSpeed +
                ", landCourse=" + landCourse +
                ", shipCourse=" + shipCourse +
                ", shipState='" + shipState + '\'' +
                ", precision='" + precion + '\'' +
                ", shipLength=" + shipLength +
                ", shipWidth=" + shipWidth +
                ", shipType='" + shipType + '\'' +
                ", aisType='" + aisType + '\'' +
                ", time=" + time +
                '}';
    }
}
