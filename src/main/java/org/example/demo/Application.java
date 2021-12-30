package org.example.demo;

import org.apache.commons.lang3.StringUtils;
import org.example.demo.monitor.SystemMonitor;
import org.phial.mybatisx.dal.event.ApplicationReadyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:
 * @project: backend-server
 * @datetime: 2021/12/27 16:30 Monday
 */
//@ImportResource({"classpath:config/spring.xml"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        String myConfig = System.getProperty("my.config");
        final Properties properties = new Properties();
        final AtomicBoolean customPropertiesLoaded = new AtomicBoolean(false);
        // 测试
        if (StringUtils.isNotBlank(myConfig)) {
            File configFile = new File(myConfig);
            if (configFile.exists()) {
                properties.load(new FileReader(configFile));
                customPropertiesLoaded.set(true);
            }
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder()
                .sources(Application.class)
                .registerShutdownHook(true);

        builder.listeners(event -> {

            if (event instanceof ApplicationEnvironmentPreparedEvent) {
                LOG.info("Application Environment Prepared");
                ConfigurableEnvironment environment = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
                String mavenProfile = environment.getProperty("application.mavenProfile");
                String activeProfiles[] = environment.getActiveProfiles();

                if (activeProfiles.length == 0) {
                    environment.addActiveProfile(mavenProfile);
                }

                if (customPropertiesLoaded.get()) {
                    environment.getPropertySources().addFirst(new PropertiesPropertySource("my-config", properties));
                }
            } else if (event instanceof ApplicationReadyEvent) {

                ConfigurableApplicationContext context = ((ApplicationReadyEvent) event).getApplicationContext();
                Map<String, ApplicationReadyListener> lmap = context.getBeansOfType(ApplicationReadyListener.class);
                if (!lmap.isEmpty()) {
                    lmap.values().forEach(l -> l.applicationReady(context));
                }

            }
        });
        ConfigurableApplicationContext context = builder.run(args);
        ConfigurableEnvironment env = context.getEnvironment();


        if (customPropertiesLoaded.get()) {
            LOG.info("Customized config specified: {}", myConfig);
        } else {
            LOG.info("No Custom config specified");
        }

        int monitorPort = env.getProperty("monitor.server.port", int.class, 6899);
        SystemMonitor monitor = new SystemMonitor(monitorPort, context);
        monitor.start();

        LOG.info("\n" +
                        "============================= APPLICATION INFORMATION =============================\n" +
                        ":: Application Name:       {}\n" +
                        ":: Build Version:          {}\n" +
                        ":: Application Version:    {}\n" +
                        ":: Maven Package Profile:  {}\n" +
                        ":: Spring Active Profiles: {}\n" +
                        ":: Logging Config:         {}\n" +
                        ":: Logging Path:           {}\n" +
                        ":: Logging File:           {}\n" +
                        ":: Server Port:            {}\n" +
                        ":: System Monitor Port:    {}\n" +
                        ":: Application Domain:     {}\n" +
                        "============================== APPLICATION STARTED!! ==============================\n",
                env.getProperty("application.name"),
                env.getProperty("app-config.build-version"),
                env.getProperty("application.version"),
                env.getProperty("application.mavenProfile"),
                StringUtils.join(env.getActiveProfiles(), ','),
                env.getProperty("logging.config"),
                env.getProperty("logging.file.path"),
                env.getProperty("logging.file.name"),
                env.getProperty("server.port"),
                monitorPort,
                env.getProperty("app-config.domain")
        );
    }
}
