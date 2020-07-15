package com.demo2.demo.respority;

import com.demo2.demo.entities.BoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface boResp extends JpaRepository<BoEntity,Integer>, JpaSpecificationExecutor<BoEntity> {
}
