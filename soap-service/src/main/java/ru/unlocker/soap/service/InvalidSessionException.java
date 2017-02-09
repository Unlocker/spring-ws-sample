package ru.unlocker.soap.service;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/**
 *
 * @author maksimovsa
 */
@SoapFault(faultCode = FaultCode.CLIENT)
public class InvalidSessionException extends Exception {

    public InvalidSessionException(String message) {
        super(message);
    }

}
