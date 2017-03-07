package ru.unlocker.soap.client.camel;

import javax.annotation.PostConstruct;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Custom Camel route builder.
 *
 * @author maksimovsa
 */
@Component
public class MyRouteBuider extends RouteBuilder {

    @Autowired
    private CamelContext camelContext;

    @Override
    public void configure() throws Exception {
        from("timer://foo?period=5s").to("bean:wsClient?method=get").to("jpa://ru.unlocker.soap.client.CountryRecord").
            log("${body}");
    }

    @PostConstruct
    public void postConstruct() throws Exception {
        camelContext.addRoutes(this);
    }

}
