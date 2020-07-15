package com.demo2.demo.respority;

import com.demo2.demo.entities.LnglatEntity;
import com.demo2.demo.entities.StaticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface obsResp extends JpaRepository<LnglatEntity,Integer>, JpaSpecificationExecutor<LnglatEntity> {
}
