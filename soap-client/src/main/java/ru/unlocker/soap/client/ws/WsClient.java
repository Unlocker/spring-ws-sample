package ru.unlocker.soap.client.ws;

import static java.lang.String.format;
import static java.net.HttpCookie.parse;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.support.MarshallingUtils;
import ru.unlocker.soap.client.Credentials;
import ru.unlocker.soap.client.GetCountryRequest;
import ru.unlocker.soap.client.GetCountryResponse;
import ru.unlocker.soap.client.MakeUnAuthRequest;
import ru.unlocker.soap.client.ObjectFactory;

/**
 *
 * @author maksimovsa
 */
public class WsClient extends WebServiceGatewaySupport {

    private static final ObjectFactory FACTORY = new ObjectFactory();
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

    protected List<String> login() {
        Credentials payload = FACTORY.createCredentials();
        payload.setUsername(username);
        payload.setPassword(password);

        final LoginMessageExtractor extractor = new LoginMessageExtractor();
        // makes WS request
        final List<String> sessionKeys = getWebServiceTemplate().sendAndReceive((WebServiceMessage request) -> {
            MarshallingUtils.marshal(getMarshaller(), payload, request);
            new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/makeAuthRequest").doWithMessage(request);
        }, extractor);
        // injects the provided keys
        AuthenticationClientInterceptor authenticator = (AuthenticationClientInterceptor) getInterceptors()[0];
        authenticator.setSessionKeys(String.join(";", sessionKeys.stream().flatMap((header) -> parse(header).stream())
            .map(c -> format("%s=%s", c.getName(), c.getValue())).collect(toList())));

        return sessionKeys;
    }

    protected GetCountryResponse select(String country) {
        GetCountryRequest payload = FACTORY.createGetCountryRequest();
        payload.setName(country);
        GetCountryResponse response = (GetCountryResponse) getWebServiceTemplate().marshalSendAndReceive(payload, new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/getCountryRequest"));
        return response;
    }

    protected void logout() {
        MakeUnAuthRequest payload = FACTORY.createMakeUnAuthRequest();
        try {
            getWebServiceTemplate().marshalSendAndReceive(payload, new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/makeUnAuthRequest"));
        } finally {
            AuthenticationClientInterceptor authenticator = (AuthenticationClientInterceptor) getInterceptors()[0];
            authenticator.setSessionKeys(null);
        }
    }

}
