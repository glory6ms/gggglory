package com.demo2.demo.respority;

import com.demo2.demo.entities.DynamicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface dynamicResp extends JpaRepository<DynamicEntity,Integer>, JpaSpecificationExecutor<DynamicEntity> {
    List<DynamicEntity> findByMmsiIn(Integer[] MMSI);

    @Query("select new DynamicEntity(d.lng,d.lat) from DynamicEntity d where d.time between ?1 and ?2")
    Collection<DynamicEntity> findBetweenTime(Date timein,Date timeout);

    @Query("select new DynamicEntity(d.mmsi,d.lng,d.lat,d.time) from DynamicEntity d where d.time between ?1 and ?2 order by d.time")
    Collection<DynamicEntity> findBetweenTime1(Date timein,Date timeout);

    //Integer mmsi, BigDecimal lng, BigDecimal lat, BigDecimal landCourse, BigDecimal landSpeed, String shipState
    @Query("select new DynamicEntity(d.mmsi,d.lng,d.lat,d.landCourse,d.landSpeed,d.shipState,d.time) from DynamicEntity d where d.time between ?1 and ?2")
    Collection<DynamicEntity> findBetweenTime2(Date timein,Date timeout);
}
