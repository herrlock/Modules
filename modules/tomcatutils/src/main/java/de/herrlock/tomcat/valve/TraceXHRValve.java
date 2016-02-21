package de.herrlock.tomcat.valve;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TraceXHRValve extends ValveBase {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The list of patterns to exclude from tracing, separated by {@code '}
     */
    protected String excludePattern = "";

    @Override
    public void invoke( final Request req, final Response res ) throws IOException, ServletException {
        LOG.entry( req, res );
        String uri = req.getRequestURI();
        LOG.debug( "URI: {}", uri );
        String[] splitPattern = this.excludePattern.split( "'" );
        LOG.debug( "patterns: {}", Arrays.toString( splitPattern ) );
        boolean matches = false;
        for ( String pattern : splitPattern ) {
            if ( uri.matches( pattern ) ) {
                LOG.debug( "Foud match, ignoring uri" );
                matches = true;
                break;
            }
        }
        if ( !matches ) {
            StringBuilder sb = new StringBuilder();
            sb.append( req.getMethod() );
            sb.append( ": " );
            sb.append( uri );
            String queryString = req.getQueryString();
            if ( queryString != null ) {
                sb.append( '?' );
                sb.append( queryString );
            }
            LOG.info( sb.toString() );
        }
        // continue
        LOG.exit();
        getNext().invoke( req, res );
    }
}
