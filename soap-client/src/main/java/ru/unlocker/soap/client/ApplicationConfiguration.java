package ru.unlocker.soap.client;

import javax.persistence.EntityManagerFactory;
import org.apache.camel.component.jpa.JpaComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import ru.unlocker.soap.client.ws.AuthenticationClientInterceptor;
import ru.unlocker.soap.client.ws.WsClient;

/**
 * Application configuration.
 *
 * @author maksimovsa
 */
@Configuration
public class ApplicationConfiguration {

    private final static String CONTEXT_PATH = "ru.unlocker.soap.client";

    /**
     * Initiates news fetcher.
     *
     * @param marshaller marshaller
     * @param factory message factory
     * @return web service client
     */
    @Bean(name = "wsClient", initMethod = "login", destroyMethod = "logout")
    public WsClient wsClient(Jaxb2Marshaller marshaller, WebServiceMessageFactory factory) {
        WsClient client = new WsClient(factory);
        client.setDefaultUri("http://127.0.0.1:8080/ws/");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setInterceptors(new ClientInterceptor[]{
            new AuthenticationClientInterceptor()
        });
        client.setUsername("username");
        client.setPassword("password");
        return client;
    }

    /**
     * SOAP message factory.
     *
     * @return message factory
     */
    @Bean
    public WebServiceMessageFactory messageFactory() {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return messageFactory;
    }

    /**
     * Marshaller.
     *
     * @return marshaller
     */
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(CONTEXT_PATH);
        return marshaller;
    }

    /**
     * JPA bean.
     *
     * @param emf entity manager factory
     * @return Camel component
     */
    @Bean(name = "jpa")
    public JpaComponent jpaComponent(EntityManagerFactory emf) {
        JpaComponent component = new JpaComponent();
        component.setEntityManagerFactory(emf);
        return component;
    }
}
