package io.varunu28.bulkheadlite;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the bulkhead limit is reached.
 * The @ResponseStatus automatically translates this to a 429 HTTP status.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class BulkheadFullException extends RuntimeException {
    public BulkheadFullException(String message) {
        super(message);
    }
}
