package org.example.demo.event;

import org.phial.mybatisx.api.entity.Entity;
import org.phial.mybatisx.dal.generator.AnnotationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EntityNotifier implements EntityEventListener {
    private static final Logger LOG = LoggerFactory.getLogger(EntityNotifier.class);

    @Override
    public boolean support(EntityEvent event) {
        Entity[] entities = event.getEntities();
        return (entities != null && entities.length > 0 && entities[0] != null);
    }

    @Override
    public void onEntityChange(EntityEvent event) {
        if (event.type() == EntityEvent.EventType.NEW ||
                event.type() == EntityEvent.EventType.UPDATE ||
                event.type() == EntityEvent.EventType.DELETE) {
            LOG.info("onEntityChange event : {}", event);
        }
    }

    /**
     * 获取待转化bean属性和描述映射关系
     *
     * @param holders
     * @return
     */
    private static Map<String, String> convert2Mapping(List<AnnotationHolder> holders) {
        Map<String, String> mappings = new HashMap<>();
        holders.forEach(e -> {
            if (e.getColumn() != null)
                mappings.put(e.getField().getName(), e.getColumn().comment());
        });
        return mappings;
    }

    /**
     * 获取属性的描述
     *
     * @param mappings
     * @param propName
     * @return
     */
    private static String propNameToDesc(Map<String, String> mappings, String propName) {
        return mappings.getOrDefault(propName, propName);
    }

}
