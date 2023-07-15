package ec.com.company.core.microservices.microserviciocalculodesahucio.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class coreException extends Exception {

    private final HttpStatus errorCode;

    public coreException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}