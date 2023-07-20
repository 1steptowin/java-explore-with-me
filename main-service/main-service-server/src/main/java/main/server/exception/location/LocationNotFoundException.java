package main.server.exception.location;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(final String message) {
        super(message);
    }
}
