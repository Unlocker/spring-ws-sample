package ru.unlocker.soap.service;

import io.spring.guides.gs_producing_web_service.AuthResult;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import io.spring.guides.gs_producing_web_service.MakeAuthRequest;
import io.spring.guides.gs_producing_web_service.MakeAuthResponse;
import io.spring.guides.gs_producing_web_service.MakeUnAuthRequest;
import io.spring.guides.gs_producing_web_service.MakeUnAuthResponse;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.addressing.server.annotation.Action;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

/**
 *
 * @author maksimovsa
 */
@Endpoint
public class CountryEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(CountryEndpoint.class);

    public static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    private static HttpServletRequest httpRequest() {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpServletConnection connection = (HttpServletConnection) context.getConnection();
        HttpServletRequest httpReq = connection.getHttpServletRequest();
        return httpReq;
    }

    private final CountryRepository countryRepository;

    @Autowired
    public CountryEndpoint(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Action(NAMESPACE_URI + "/makeAuthRequest")
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "makeAuthRequest")
    @ResponsePayload
    public MakeAuthResponse makeAuth(@RequestPayload MakeAuthRequest request) {
        HttpServletRequest httpReq = httpRequest();

        HttpSession session = httpReq.getSession();
        //setting session to expiry in 30 mins
        session.setMaxInactiveInterval(30 * 60);
        LOG.info("Accepted SESSION {}", session);

        final MakeAuthResponse response = new MakeAuthResponse();
        response.setAuthResult(AuthResult.SUCCESS);
        return response;
    }

    @Action(NAMESPACE_URI + "/getCountryRequest")
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) throws InvalidSessionException {
        HttpServletRequest httpReq = httpRequest();

        HttpSession session = httpReq.getSession(false);
        LOG.info("Received SESSION {}", session);
        if (session == null) {
            throw new InvalidSessionException("Invalid user session");
        }

        GetCountryResponse response = new GetCountryResponse();
        response.setCountry(countryRepository.findCountry(request.getName()));
        return response;
    }

    @Action(NAMESPACE_URI + "/makeUnAuthRequest")
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "makeUnAuthRequest")
    @ResponsePayload
    public MakeUnAuthResponse makeUnAuth(@RequestPayload MakeUnAuthRequest request) {
        HttpServletRequest httpReq = httpRequest();

        HttpSession session = httpReq.getSession(false);
        LOG.info("Received SESSION {}", session);
        Optional.ofNullable(session).ifPresent(HttpSession::invalidate);

        final MakeUnAuthResponse response = new MakeUnAuthResponse();
        response.setAuthResult(AuthResult.SUCCESS);
        return response;
    }
}
