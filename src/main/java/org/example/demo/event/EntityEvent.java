package org.example.demo.event;

import org.phial.mybatisx.api.entity.Entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 实体CRUD事件
 * @since 2020-05-17
 * @author phial
 */
public class EntityEvent implements Serializable {

    private static final long serialVersionUID = 226282962739879916L;

    private Entity[] entities;

    private EventType type;

    public EntityEvent(EventType type, Entity ... entities) {
        this.type = type;
        this.entities = entities;
    }

    public enum EventType {
        NEW,        // 新增一个实体时触发
        UPDATE,     // 更新一个实体时触发
        DELETE      // 删除一个实体时触发
    }

    public EventType type() {
        return type;
    }

    public Entity [] entities() {
        if (entities == null) return new Entity[0];
        return entities;
    }

    /**
     * 获取 entities
     *
     * @return entities
     */
    public Entity[] getEntities() {
        return entities;
    }

    /**
     * 设置 entities
     *
     * @param entities entities 值
     */
    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }

    /**
     * 获取 type
     *
     * @return type
     */
    public EventType getType() {
        return type;
    }

    /**
     * 设置 type
     *
     * @param type type 值
     */
    public void setType(EventType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "EntityEvent{" +
                "entities=" + Arrays.toString(entities) +
                ", type=" + type +
                '}';
    }
}
