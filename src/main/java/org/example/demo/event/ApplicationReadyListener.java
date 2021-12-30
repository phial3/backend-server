package org.example.demo.event;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationReadyListener {

    void applicationReady(ConfigurableApplicationContext context);

}
