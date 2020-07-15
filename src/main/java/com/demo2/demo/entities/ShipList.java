package com.demo2.demo.entities;

import java.util.Collection;

public class ShipList {
    Collection<DynamicEntity> dynamicEntities;
    Collection<StaticEntity> staticEntities;

    public ShipList(Collection<DynamicEntity> dynamicEntities, Collection<StaticEntity> staticEntities) {
        this.dynamicEntities = dynamicEntities;
        this.staticEntities = staticEntities;
    }

    @Override
    public String toString() {
        return "ShipList{" +
                "dynamicEntities=" + dynamicEntities +
                ", staticEntities=" + staticEntities +
                '}';
    }

    public Collection<DynamicEntity> getDynamicEntities() {
        return dynamicEntities;
    }

    public void setDynamicEntities(Collection<DynamicEntity> dynamicEntities) {
        this.dynamicEntities = dynamicEntities;
    }

    public Collection<StaticEntity> getStaticEntities() {
        return staticEntities;
    }

    public void setStaticEntities(Collection<StaticEntity> staticEntities) {
        this.staticEntities = staticEntities;
    }
}
