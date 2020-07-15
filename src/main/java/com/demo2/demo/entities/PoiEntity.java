package com.demo2.demo.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "poi", schema = "ais")
public class PoiEntity {
    private int id;
    private String year;
    private String mon;
    private String day;
    private String a1;
    private String a2;
    private String a3;
    private String a4;
    private String a5;
    private String a6;
    private String a7;
    private String a8;
    private String a9;
    private String a10;
    private String a11;
    private String a12;
    private String a13;
    private String a14;
    private String a15;
    private String a16;
    private String a17;
    private String a18;
    private String a19;
    private String a20;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "year", nullable = true, length = 255)
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Basic
    @Column(name = "mon", nullable = true, length = 255)
    public String getMon() {
        return mon;
    }

    public void setMon(String mon) {
        this.mon = mon;
    }

    @Basic
    @Column(name = "day", nullable = true, length = 255)
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Basic
    @Column(name = "a1", nullable = true, length = 255)
    public String getA1() {
        return a1;
    }

    public void setA1(String a1) {
        this.a1 = a1;
    }

    @Basic
    @Column(name = "a2", nullable = true, length = 255)
    public String getA2() {
        return a2;
    }

    public void setA2(String a2) {
        this.a2 = a2;
    }

    @Basic
    @Column(name = "a3", nullable = true, length = 255)
    public String getA3() {
        return a3;
    }

    public void setA3(String a3) {
        this.a3 = a3;
    }

    @Basic
    @Column(name = "a4", nullable = true, length = 255)
    public String getA4() {
        return a4;
    }

    public void setA4(String a4) {
        this.a4 = a4;
    }

    @Basic
    @Column(name = "a5", nullable = true, length = 255)
    public String getA5() {
        return a5;
    }

    public void setA5(String a5) {
        this.a5 = a5;
    }

    @Basic
    @Column(name = "a6", nullable = true, length = 255)
    public String getA6() {
        return a6;
    }

    public void setA6(String a6) {
        this.a6 = a6;
    }

    @Basic
    @Column(name = "a7", nullable = true, length = 255)
    public String getA7() {
        return a7;
    }

    public void setA7(String a7) {
        this.a7 = a7;
    }

    @Basic
    @Column(name = "a8", nullable = true, length = 255)
    public String getA8() {
        return a8;
    }

    public void setA8(String a8) {
        this.a8 = a8;
    }

    @Basic
    @Column(name = "a9", nullable = true, length = 255)
    public String getA9() {
        return a9;
    }

    public void setA9(String a9) {
        this.a9 = a9;
    }

    @Basic
    @Column(name = "a10", nullable = true, length = 255)
    public String getA10() {
        return a10;
    }

    public void setA10(String a10) {
        this.a10 = a10;
    }

    @Basic
    @Column(name = "a11", nullable = true, length = 255)
    public String getA11() {
        return a11;
    }

    public void setA11(String a11) {
        this.a11 = a11;
    }

    @Basic
    @Column(name = "a12", nullable = true, length = 255)
    public String getA12() {
        return a12;
    }

    public void setA12(String a12) {
        this.a12 = a12;
    }

    @Basic
    @Column(name = "a13", nullable = true, length = 255)
    public String getA13() {
        return a13;
    }

    public void setA13(String a13) {
        this.a13 = a13;
    }

    @Basic
    @Column(name = "a14", nullable = true, length = 255)
    public String getA14() {
        return a14;
    }

    public void setA14(String a14) {
        this.a14 = a14;
    }

    @Basic
    @Column(name = "a15", nullable = true, length = 255)
    public String getA15() {
        return a15;
    }

    public void setA15(String a15) {
        this.a15 = a15;
    }

    @Basic
    @Column(name = "a16", nullable = true, length = 255)
    public String getA16() {
        return a16;
    }

    public void setA16(String a16) {
        this.a16 = a16;
    }

    @Basic
    @Column(name = "a17", nullable = true, length = 255)
    public String getA17() {
        return a17;
    }

    public void setA17(String a17) {
        this.a17 = a17;
    }

    @Basic
    @Column(name = "a18", nullable = true, length = 255)
    public String getA18() {
        return a18;
    }

    public void setA18(String a18) {
        this.a18 = a18;
    }

    @Basic
    @Column(name = "a19", nullable = true, length = 255)
    public String getA19() {
        return a19;
    }

    public void setA19(String a19) {
        this.a19 = a19;
    }

    @Basic
    @Column(name = "a20", nullable = true, length = 255)
    public String getA20() {
        return a20;
    }

    public void setA20(String a20) {
        this.a20 = a20;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoiEntity poiEntity = (PoiEntity) o;
        return id == poiEntity.id &&
                Objects.equals(year, poiEntity.year) &&
                Objects.equals(mon, poiEntity.mon) &&
                Objects.equals(day, poiEntity.day) &&
                Objects.equals(a1, poiEntity.a1) &&
                Objects.equals(a2, poiEntity.a2) &&
                Objects.equals(a3, poiEntity.a3) &&
                Objects.equals(a4, poiEntity.a4) &&
                Objects.equals(a5, poiEntity.a5) &&
                Objects.equals(a6, poiEntity.a6) &&
                Objects.equals(a7, poiEntity.a7) &&
                Objects.equals(a8, poiEntity.a8) &&
                Objects.equals(a9, poiEntity.a9) &&
                Objects.equals(a10, poiEntity.a10) &&
                Objects.equals(a11, poiEntity.a11) &&
                Objects.equals(a12, poiEntity.a12) &&
                Objects.equals(a13, poiEntity.a13) &&
                Objects.equals(a14, poiEntity.a14) &&
                Objects.equals(a15, poiEntity.a15) &&
                Objects.equals(a16, poiEntity.a16) &&
                Objects.equals(a17, poiEntity.a17) &&
                Objects.equals(a18, poiEntity.a18) &&
                Objects.equals(a19, poiEntity.a19) &&
                Objects.equals(a20, poiEntity.a20);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, mon, day, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20);
    }
}
