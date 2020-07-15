package com.demo2.demo.respority;

import com.demo2.demo.entities.DynamicEntity;
import com.demo2.demo.entities.Sheet1Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface modalResp extends JpaRepository<Sheet1Entity,Integer>, JpaSpecificationExecutor<Sheet1Entity> {
}
