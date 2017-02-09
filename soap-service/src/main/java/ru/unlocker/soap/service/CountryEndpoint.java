package ru.unlocker.soap.service;

import io.spring.guides.gs_producing_web_service.AuthResult;
import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;
import io.spring.guides.gs_producing_web_service.MakeAuthRequest;
import io.spring.guides.gs_producing_web_service.MakeAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

/**
 *
 * @author maksimovsa
 */
@Endpoint
public class CountryEndpoint {

    public static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    private final CountryRepository countryRepository;

    @Autowired
    public CountryEndpoint(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @SoapAction(NAMESPACE_URI + "/MakeAuthRequest")
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "makeAuthRequest")
    @ResponsePayload
    public MakeAuthResponse makeAuth(@RequestPayload MakeAuthRequest request) {
        final MakeAuthResponse response = new MakeAuthResponse();
        response.setAuthResult(AuthResult.SUCCESS);
        return response;
    }

    @SoapAction(NAMESPACE_URI + "/GetCountryRequest")
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
        GetCountryResponse response = new GetCountryResponse();
        response.setCountry(countryRepository.findCountry(request.getName()));
        return response;
    }
}
