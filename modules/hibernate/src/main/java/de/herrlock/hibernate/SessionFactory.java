package de.herrlock.hibernate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.cfg.Configuration;

/**
 * 
 * @author HerrLock
 */
public class SessionFactory {
    private static final Logger LOG = LogManager.getLogger();
    public static final String CLASS_LIST_PROPERTY = "de.herrlock.hibernate.classList";
    public static final String CLASS_LIST_DEFAULT_FILE = "/de/herrlock/hibernate/classList.txt";

    private static org.hibernate.SessionFactory sessionFactory;
    private static SessionStatus sessionStatus = SessionStatus.UNSET;

    private SessionFactory() {

    }

    private static synchronized org.hibernate.SessionFactory getSessionFactory( final SessionStatus status,
        final String configfileName ) {
        LOG.entry( configfileName );
        if ( sessionFactory == null ) {
            LOG.info( "creating sessionFactory" );
            Configuration configuration = new Configuration();
            List<String> dtoClasses = getDtoClasses();
            for ( String dtoClassName : dtoClasses ) {
                LOG.info( "Add annotated class: {}", dtoClassName );
                try {
                    Class<?> dtoClass = Class.forName( dtoClassName );
                    configuration.addAnnotatedClass( dtoClass );
                } catch ( ClassNotFoundException ex ) {
                    LOG.error( new ParameterizedMessage( "Could not find class {}", dtoClasses ), ex );
                }
            }
            configuration.configure( configfileName );

            sessionStatus = status;
            sessionFactory = configuration.buildSessionFactory();
        }
        if ( sessionStatus != status ) {
            LOG.warn( "returning SessionFactory with SessionStatus {} but requested was {}", sessionStatus, status );
        }
        return sessionFactory;
    }

    private static List<String> getDtoClasses() {
        String classListLocation = System.getProperty( CLASS_LIST_PROPERTY );
        if ( classListLocation == null ) {
            // no custom location set, use default-location
            return loadDtoClassesFrom( SessionFactory.class.getResource( CLASS_LIST_DEFAULT_FILE ) );
        }
        /* classListLocation != null */
        // custom location set, use given location
        URL resource = SessionFactory.class.getResource( classListLocation );
        if ( resource == null ) {
            // custom class-list cannot be found
            return loadDtoClassesFrom( SessionFactory.class.getResource( CLASS_LIST_DEFAULT_FILE ) );
        }
        /* resource != null */
        // custom class-list could be located
        return loadDtoClassesFrom( resource );
    }

    private static List<String> loadDtoClassesFrom( URL url ) {
        LOG.info( "Loading DTO-classes from: {}", url );
        List<String> result = new ArrayList<>();
        if ( url != null ) {
            try ( BufferedReader reader = new BufferedReader(
                new InputStreamReader( url.openStream(), StandardCharsets.UTF_8 ) ) ) {
                String nextLine;
                while ( ( nextLine = reader.readLine() ) != null ) {
                    if ( !nextLine.trim().isEmpty() ) {
                        result.add( nextLine.trim() );
                    }
                }
            } catch ( IOException ex ) {
                LOG.fatal( "Could not read from URL", ex );
            }
        }
        return result;
    }

    public static Session getSession() {
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.PROD, "hibernate.cfg.xml" ).withOptions();
        return sessionBuilder.openSession();
    }

    public static synchronized Session getTestSession() {
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.TEST, "hibernate.test.cfg.xml" ).withOptions();
        return sessionBuilder.openSession();
    }

    public static synchronized void close() {
        if ( sessionFactory != null ) {
            sessionFactory.close();
            sessionFactory = null;
            sessionStatus = SessionStatus.UNSET;
        }
    }

    private static enum SessionStatus {
        UNSET, PROD, TEST;
    }
}
