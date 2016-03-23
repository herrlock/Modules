package de.herrlock.tomcat.valve;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RandomDelayValve extends ValveBase {
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The file where the configuration is stored
     */
    protected String delayFile = "conf/delay.properties";
    /**
     * The default-delay that is always waited
     */
    protected int defaultDelay = 2000;
    /**
     * The maximum random delay added to the default delay
     */
    protected int randomDelay = 1000;
    /**
     * The list of patterns to exclude from tracing, separated by {@code '}
     */
    protected String excludePatterns = "";

    private final Random random = new Random();

    @Override
    public void invoke( final Request request, final Response response ) throws IOException, ServletException {
        String uri = request.getRequestURI();
        Properties delayConf = loadConfigFromDelayFile();
        int defaultDelay = Integer.parseInt( delayConf.getProperty( "default", String.valueOf( this.defaultDelay ) ) );
        int randomDelay = Integer.parseInt( delayConf.getProperty( "random", String.valueOf( this.randomDelay ) ) );
        boolean enabled = Boolean.parseBoolean( delayConf.getProperty( "enabled", "false" ) );

        if ( enabled ) {
            boolean matches = false;
            String[] patterns = this.excludePatterns.split( "'" );
            for ( String string : patterns ) {
                if ( uri.contains( string ) ) {
                    matches = true;
                    break;
                }
            }
            if ( matches ) {
                long delay = defaultDelay + this.random.nextInt( randomDelay );
                LOG.info( "{}: {}   [{} ms]", request.getMethod(), uri, delay );
                try {
                    Thread.sleep( delay );
                } catch ( InterruptedException e ) {
                    LOG.error( "someone waked me up : " + e.getMessage() );
                }
            }
        }
        // continue
        getNext().invoke( request, response );
    }

    private Properties loadConfigFromDelayFile() {
        final Properties p = new Properties();
        try ( FileInputStream configStream = new FileInputStream(
            new File( System.getProperty( "catalina.home" ), this.delayFile ) ) ) {
            p.load( configStream );
        } catch ( IOException e ) {
            LOG.error( e );
        }
        return p;
    }
}
