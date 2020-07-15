package com.demo2.demo.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "bo", schema = "ais")
public class BoEntity {
    private Date time;
    private BigDecimal hpcp;
    private int cid;

    @Basic
    @Column(name = "time", nullable = true)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Basic
    @Column(name = "HPCP", nullable = true, precision = 0)
    public BigDecimal getHpcp() {
        return hpcp;
    }

    public void setHpcp(BigDecimal hpcp) {
        this.hpcp = hpcp;
    }

    @Id
    @Column(name = "cid", nullable = false)
    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoEntity boEntity = (BoEntity) o;
        return cid == boEntity.cid &&
                Objects.equals(time, boEntity.time) &&
                Objects.equals(hpcp, boEntity.hpcp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, hpcp, cid);
    }
}
