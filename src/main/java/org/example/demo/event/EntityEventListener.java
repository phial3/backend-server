package org.example.demo.event;


public interface EntityEventListener {

    void onEntityChange(EntityEvent event);

    boolean support(EntityEvent event);

}
