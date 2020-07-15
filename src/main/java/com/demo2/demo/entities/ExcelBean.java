package com.demo2.demo.entities;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.handler.inter.IExcelModel;

import java.math.BigDecimal;

public class ExcelBean implements IExcelModel {
    @Excel(name = "mmsi")
    private Integer mmsi1;
    @Excel(name = "对地航速")
    private BigDecimal landSpeed1;
    @Excel(name = "对地航行")
    private BigDecimal landCourse1;

    @Excel(name="precipitation")
    private BigDecimal precipitation;

    @Excel(name = "日期")
    private String time;
    @Excel(name = "name")
    private int name;

    @Excel(name = "precipition")
    private BigDecimal precipition;
    @Excel(name = "days")
    private int days;
    @Excel(name = "p95")
    private Double p95;

    @Excel(name = "p95p")
    private Double p95p;

    public ExcelBean(int name, BigDecimal precipition, int days, Double p95, Double p95p) {
        this.name = name;
        this.precipition = precipition;
        this.days = days;
        this.p95 = p95;
        this.p95p = p95p;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public BigDecimal getPrecipition() {
        return precipition;
    }

    public void setPrecipition(BigDecimal precipition) {
        this.precipition = precipition;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Double getP95() {
        return p95;
    }

    public void setP95(Double p95) {
        this.p95 = p95;
    }

    public Double getP95p() {
        return p95p;
    }

    public void setP95p(Double p95p) {
        this.p95p = p95p;
    }

    public ExcelBean(Integer mmsi1, BigDecimal landSpeed1, BigDecimal landCourse1) {
        this.mmsi1 = mmsi1;
        this.landSpeed1 = landSpeed1;
        this.landCourse1 = landCourse1;
    }

    public ExcelBean() {
    }


    public ExcelBean(BigDecimal precipitation, String time) {
        this.precipitation = precipitation;
        this.time = time;
    }

    public ExcelBean(BigDecimal precipitation) {
        this.precipitation = precipitation;
    }

    public BigDecimal getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(BigDecimal precipitation) {
        this.precipitation = precipitation;
    }

    public Integer getMmsi1() {
        return mmsi1;
    }

    public void setMmsi1(Integer mmsi1) {
        this.mmsi1 = mmsi1;
    }

    public BigDecimal getLandSpeed1() {
        return landSpeed1;
    }

    public void setLandSpeed1(BigDecimal landSpeed1) {
        this.landSpeed1 = landSpeed1;
    }

    public BigDecimal getLandCourse1() {
        return landCourse1;
    }

    public void setLandCourse1(BigDecimal landCourse1) {
        this.landCourse1 = landCourse1;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }

    @Override
    public void setErrorMsg(String s) {

    }

    @Override
    public String toString() {
        return "ExcelBean{" +
                "mmsi1=" + mmsi1 +
                ", landSpeed1=" + landSpeed1 +
                ", landCourse1=" + landCourse1 +
                '}';
    }
}
