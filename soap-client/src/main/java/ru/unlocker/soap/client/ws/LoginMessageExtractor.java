package ru.unlocker.soap.client.ws;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterator.SORTED;
import static java.util.Spliterators.spliteratorUnknownSize;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.xml.transform.TransformerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

/**
 * Extracts authentication cookies from the login response.
 *
 * @author maksimovsa
 */
public class LoginMessageExtractor implements WebServiceMessageExtractor<List<String>> {

    /**
     * Log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(LoginMessageExtractor.class);

    @Override
    public List<String> extractData(WebServiceMessage message) throws IOException, TransformerException {
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpUrlConnection conn = (HttpUrlConnection) context.getConnection();
        Iterator<String> cookies = conn.getResponseHeaders("Set-Cookie");

        Spliterator<String> spliterator = spliteratorUnknownSize(cookies, DISTINCT | SORTED | ORDERED);
        List<String> authHeaders = StreamSupport.stream(spliterator, false)
            .filter(c -> c.startsWith("JSESSIONID")).collect(Collectors.toList());

        LOG.debug("AUTH COOKIES: {}", Arrays.toString(authHeaders.toArray()));
        return authHeaders;
    }
}
