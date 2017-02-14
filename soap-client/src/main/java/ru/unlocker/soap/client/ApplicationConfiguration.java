package ru.unlocker.soap.client;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
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
 *
 * @author maksimovsa
 */
@Configuration
public class ApplicationConfiguration {

    private final static String CONTEXT_PATH = "ru.unlocker.soap.client";

    @Autowired
    CamelContext camelContext;

    /**
     * Initiates news fetcher.
     */
    @Bean
    public WsClient newsClient(Jaxb2Marshaller marshaller, WebServiceMessageFactory factory) {
        WsClient client = new WsClient(factory);
        client.setDefaultUri("http://127.0.0.1:8080");
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

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(CONTEXT_PATH);
        return marshaller;
    }
}
