package org.example.demo.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@Component
public class EntityEventDispatcher implements ApplicationReadyListener  {

    private static final Logger LOG = LoggerFactory.getLogger(EntityEventDispatcher.class);

    private Collection<EntityEventListener> listeners;

    private ThreadPoolTaskExecutor executor;

    public EntityEventDispatcher(ThreadPoolTaskExecutor executor) {
        this.executor = executor;
    }

    public void fireEntityEvent(final EntityEvent event) {
        if (CollectionUtils.isEmpty(listeners)){
            return;
        }
        listeners.forEach(e -> {
            if (e.support(event)) {
                executor.submit(() -> e.onEntityChange(event));
            }
        });
    }

    @Override
    public void applicationReady(ConfigurableApplicationContext context) {
        listeners = context.getBeansOfType(EntityEventListener.class).values();
        LOG.info("::::::::FIND Dispatcher::::::::EntityEventListeners: {}", listeners.size());
    }
}
