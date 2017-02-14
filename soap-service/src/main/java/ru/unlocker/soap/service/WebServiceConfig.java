package ru.unlocker.soap.service;

import java.util.List;
import java.util.Properties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import static ru.unlocker.soap.service.CountryEndpoint.NAMESPACE_URI;

/**
 *
 * @author maksimovsa
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext appCtx) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(appCtx);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean
    public WebServiceMessageFactory messageFactory() {
        SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
        messageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return messageFactory;
    }

    @Bean(name = "countries")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setCreateSoap11Binding(false);
        definition.setCreateSoap12Binding(true);
        definition.setPortTypeName("CountriesPort");
        definition.setLocationUri("/ws");
        definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
        definition.setSchema(countriesSchema);

        // fix for adding soapAction to the dynamic WSDL
        Properties soapActions = new Properties();
        soapActions.setProperty("makeAuth", NAMESPACE_URI + "/makeAuthRequest");
        soapActions.setProperty("makeUnAuth", NAMESPACE_URI + "/makeUnAuthRequest");
        soapActions.setProperty("getCountry", NAMESPACE_URI + "/getCountryRequest");
        definition.setSoapActions(soapActions);
        return definition;
    }

    @Bean
    public XsdSchema countriesSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xjc/countries.xsd"));
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
    }

}
