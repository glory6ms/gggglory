package com.demo2.demo.entities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Period {


    private String timein;
    private String timeout;
    SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public String getTimein() {
        return timein;
    }

    public void setTimein(String timein) {
        this.timein = timein;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Period)) return false;
        Period period = (Period) o;
        return Objects.equals(getTimein(), period.getTimein()) &&
                Objects.equals(getTimeout(), period.getTimeout()) &&
                Objects.equals(ft2, period.ft2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimein(), getTimeout(), ft2);
    }

    @Override
    public String toString() {
        return "Period{" +
                "timein='" + timein + '\'' +
                ", timeout='" + timeout + '\'' +
                ", ft2=" + ft2 +
                '}';
    }

    public Period(String timein, String timeout) {
        this.timein = timein;
        this.timeout = timeout;
    }

    public Period() {
    }
}
