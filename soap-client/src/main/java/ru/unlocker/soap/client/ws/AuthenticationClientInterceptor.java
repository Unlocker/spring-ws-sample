package ru.unlocker.soap.client.ws;

import java.io.IOException;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

/**
 * Client interceptor makes session cookie injection.
 *
 * @author maksimovsa
 */
public class AuthenticationClientInterceptor implements ClientInterceptor {

    /**
     * Session keys.
     */
    private String sessionKeys;

    /**
     * Gets the session keys.
     *
     * @return session keys
     */
    public Optional<String> getSessionKeys() {
        return ofNullable(sessionKeys);
    }

    /**
     * Sets the session keys.
     *
     * @param sessionKeys session keys
     */
    public void setSessionKeys(String sessionKeys) {
        this.sessionKeys = sessionKeys;
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) throws WebServiceClientException {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
        if (getSessionKeys().isPresent()) {
            final String value = getSessionKeys().get();
            try {
                connection.addRequestHeader("Cookie", value);
            } catch (IOException e) {
                throw new RuntimeException("Unable to set the cookies: " + value, e);
            }
        }

        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
    }

}
