package de.herrlock.jersey;

import org.glassfish.jersey.server.ResourceConfig;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        packages( true, getClass().getPackage().getName() );
    }
}
