package main.server.exception.featureLocation;

public class FeatureLocationNotFoundException extends RuntimeException {
    public FeatureLocationNotFoundException(final String message) {
        super(message);
    }
}
