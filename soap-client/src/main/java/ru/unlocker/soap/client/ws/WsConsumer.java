package ru.unlocker.soap.client.ws;

import java.util.concurrent.ScheduledExecutorService;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

/**
 *
 * @author maksimovsa
 */
public class WsConsumer extends ScheduledPollConsumer {

    public WsConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    public WsConsumer(Endpoint endpoint, Processor processor, ScheduledExecutorService scheduledExecutorService) {
        super(endpoint, processor, scheduledExecutorService);
    }

    @Override
    protected int poll() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
