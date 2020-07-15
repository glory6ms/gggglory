package com.demo2.demo.respority;

import com.demo2.demo.entities.PoiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;

public interface poiResp extends JpaRepository<PoiEntity,Integer> {

}
