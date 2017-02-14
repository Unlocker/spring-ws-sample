package ru.unlocker.soap.client.ws;

import static java.lang.String.format;
import static java.net.HttpCookie.parse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.support.MarshallingUtils;
import ru.unlocker.soap.client.CountryRecord;
import ru.unlocker.soap.client.Credentials;
import ru.unlocker.soap.client.GetCountryRequest;
import ru.unlocker.soap.client.GetCountryResponse;
import ru.unlocker.soap.client.MakeAuthRequest;
import ru.unlocker.soap.client.MakeUnAuthRequest;
import ru.unlocker.soap.client.ObjectFactory;

/**
 *
 * @author maksimovsa
 */
public class WsClient extends WebServiceGatewaySupport implements Supplier<CountryRecord> {

    private static final ObjectFactory FACTORY = new ObjectFactory();
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final List<String> IDS = Collections.unmodifiableList(Arrays.asList("Spain", "United Kingdom",
        "Poland"));

    private String username = "username";
    private String password = "password";

    public WsClient() {
    }

    public WsClient(WebServiceMessageFactory messageFactory) {
        super(messageFactory);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected void login() {
        MakeAuthRequest payload = FACTORY.createMakeAuthRequest();
        Credentials creds = FACTORY.createCredentials();
        creds.setUsername(username);
        creds.setPassword(password);
        payload.setAuth(creds);

        final LoginMessageExtractor extractor = new LoginMessageExtractor();
        // makes WS request
        final List<String> sessionKeys = getWebServiceTemplate().sendAndReceive((WebServiceMessage request) -> {
            MarshallingUtils.marshal(getMarshaller(), payload, request);
            new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/makeAuthRequest").doWithMessage(
                request);
        }, extractor);
        // injects the provided keys
        AuthenticationClientInterceptor authenticator = (AuthenticationClientInterceptor) getInterceptors()[0];
        authenticator.setSessionKeys(String.join(";", sessionKeys.stream().flatMap((header) -> parse(header).stream())
            .map(c -> format("%s=%s", c.getName(), c.getValue())).collect(toList())));

//        return sessionKeys;
    }

    protected GetCountryResponse select(String country) {
        GetCountryRequest payload = FACTORY.createGetCountryRequest();
        payload.setName(country);
        GetCountryResponse response = (GetCountryResponse) getWebServiceTemplate().marshalSendAndReceive(payload,
            new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/getCountryRequest"));
        return response;
    }

    protected void logout() {
        MakeUnAuthRequest payload = FACTORY.createMakeUnAuthRequest();
        try {
            getWebServiceTemplate().marshalSendAndReceive(payload, new SoapActionCallback(
                "http://spring.io/guides/gs-producing-web-service/makeUnAuthRequest"));
        } finally {
            AuthenticationClientInterceptor authenticator = (AuthenticationClientInterceptor) getInterceptors()[0];
            authenticator.setSessionKeys(null);
        }
    }

    @Override
    public CountryRecord get() {
        String arg = IDS.get(RANDOM.nextInt(IDS.size()));
        GetCountryResponse resp = select(arg);
        CountryRecord cr = new CountryRecord();
        cr.setName(resp.getCountry().getName());
        cr.setCapital(resp.getCountry().getCapital());
        cr.setCurrency(resp.getCountry().getCurrency().value());
        cr.setPopulation(resp.getCountry().getPopulation());
        return cr;
    }

}
