package com.demo2.demo.respority;
import com.demo2.demo.entities.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
@Transactional
public interface userResp extends JpaRepository<UsersEntity,Integer>, JpaSpecificationExecutor<UsersEntity> {
    UsersEntity findByUsername(String username);

    @Query(value = "update UsersEntity us set us.lastLng = ?1 ,us.latLat = ?2 where us.username=?3")
    @Modifying
    @Transactional
    void save1(String last_lng,String last_lat,String username);
}
