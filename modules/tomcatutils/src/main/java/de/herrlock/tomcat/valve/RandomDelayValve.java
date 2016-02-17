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

public class RandomDelayValve extends ValveBase {

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

    @Override
    public void invoke( final Request request, final Response response ) throws IOException, ServletException {
        String uri = request.getRequestURI();
        Properties delayConf = loadConfigFromDelayFile();
        int defaultDelay = Integer.parseInt( delayConf.getProperty( "default", "" + this.defaultDelay ) );
        int randomDelay = Integer.parseInt( delayConf.getProperty( "random", "" + this.randomDelay ) );
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
                long delay = defaultDelay + new Random().nextInt( randomDelay );
                System.out.println( request.getMethod() + ": " + uri + "   [" + delay + " ms]" );
                try {
                    Thread.sleep( delay );
                } catch ( InterruptedException e ) {
                    System.err.println( "someone waked me up : " + e.getMessage() );
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
        } catch ( NullPointerException | IOException e ) {
            System.err.println( e );
        }
        return p;
    }
}