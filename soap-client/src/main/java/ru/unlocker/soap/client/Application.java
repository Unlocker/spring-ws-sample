package ru.unlocker.soap.client;

import org.apache.camel.spring.boot.CamelSpringBootApplicationController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application entry point.
 *
 * @author maksimovsa
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"ru.unlocker.soap.client"})
public class Application implements CommandLineRunner {

    public static void main(String... args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        CamelSpringBootApplicationController applicationController
            = applicationContext.getBean(CamelSpringBootApplicationController.class);
        applicationController.run();
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
