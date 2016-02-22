package de.herrlock.hibernate;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.cfg.Configuration;

public class SessionFactoryWrapper implements Closeable {
    private static final Logger LOG = LogManager.getLogger();

    public static final String PROD_SESSION_FILE = "hibernate.cfg.xml";
    public static final String TEST_SESSION_FILE = "hibernate.test.cfg.xml";
    public static final String CLASS_LIST_PROPERTY = "de.herrlock.hibernate.classList";
    public static final String CLASS_LIST_DEFAULT_FILE = "/de/herrlock/hibernate/classList.txt";

    public static final SessionFactoryWrapper SFW = new SessionFactoryWrapper();

    private SessionFactory sessionFactory;

    /**
     * prevent instantiation
     */
    private SessionFactoryWrapper() {
        // singleton
    }

    /**
     * Creates a Session from the configuration-file {@value PROD_SESSION_FILE}
     * 
     * @return a session
     */
    public Session getSession() {
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.PROD, PROD_SESSION_FILE ).withOptions();
        return sessionBuilder.openSession();
    }

    /**
     * Creates a Session from the configuration-file {@value TEST_SESSION_FILE}
     * 
     * @return a test-session
     */
    public Session getTestSession() {
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.TEST, TEST_SESSION_FILE ).withOptions();
        return sessionBuilder.openSession();
    }

    private synchronized org.hibernate.SessionFactory getSessionFactory( final SessionStatus status,
        final String configfileName ) {
        LOG.entry( configfileName );
        if ( this.sessionFactory == null ) {
            this.sessionFactory = new SessionFactory( status, configfileName );
        }
        if ( this.sessionFactory.getStatus() != status ) {
            LOG.warn( "returning SessionFactory with SessionStatus {} but requested was {}", this.sessionFactory.getStatus(),
                status );
        }
        return this.sessionFactory.getSessionFactory();
    }

    private static List<String> getDtoClasses() {
        String classListLocation = System.getProperty( CLASS_LIST_PROPERTY );
        if ( classListLocation == null ) {
            // no custom location set, use default-location
            return loadDtoClassesFrom( SessionFactoryWrapper.class.getResource( CLASS_LIST_DEFAULT_FILE ) );
        }
        /* classListLocation != null */
        // custom location set, use given location
        URL resource = SessionFactoryWrapper.class.getResource( classListLocation );
        if ( resource == null ) {
            // custom class-list cannot be found
            return loadDtoClassesFrom( SessionFactoryWrapper.class.getResource( CLASS_LIST_DEFAULT_FILE ) );
        }
        /* resource != null */
        // custom class-list could be located
        return loadDtoClassesFrom( resource );
    }

    @Override
    public void close() {
        if ( this.sessionFactory != null ) {
            this.sessionFactory.close();
            this.sessionFactory = null;
        }
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "SessionFactoryWrapper (Factory: {0})", this.sessionFactory );
    }

    /**
     * Reads all lines from the resource at the given {@link URL}, skips empty lines (where {@code line.trim().isEmpty() == true}
     * ). Assumes the input is UTF-8
     * 
     * @param url
     *            the url to read from
     * @return a list of classes that are supposed to be DTO-classes for Hibernate
     */
    private static List<String> loadDtoClassesFrom( URL url ) {
        return loadDtoClassesFrom( url, StandardCharsets.UTF_8 );
    }

    /**
     * Reads all lines from the resource at the given {@link URL}, skips empty lines (where {@code line.trim().isEmpty() == true}
     * ).
     * 
     * @param url
     *            the url to read from
     * @param charset
     *            the charset to read with
     * @return a list of classes that are supposed to be DTO-classes for Hibernate
     */
    private static List<String> loadDtoClassesFrom( final URL url, final Charset charset ) {

        LOG.info( "Loading DTO-classes from: {}", url );
        List<String> result = new ArrayList<>();
        if ( url != null ) {
            try ( BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), charset ) ) ) {
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

    /**
     * 
     * @author HerrLock
     */
    public static class SessionFactory implements AutoCloseable {

        private SessionStatus status;
        private org.hibernate.SessionFactory sessionFactory;
        private boolean open = true;

        private SessionFactory( final SessionStatus status, final String configfileName ) {
            this.status = status;
            this.sessionFactory = buildSessionFactory( configfileName );
        }

        private org.hibernate.SessionFactory buildSessionFactory( final String configfileName ) {
            LOG.entry( configfileName );
            if ( this.sessionFactory == null ) {
                LOG.info( "creating sessionFactory" );
                Configuration configuration = new Configuration();

                // load dto-classes
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

                // complete the configuration with the given file
                configuration.configure( configfileName );

                this.sessionFactory = configuration.buildSessionFactory();
            }
            return this.sessionFactory;
        }

        public SessionStatus getStatus() {
            return this.status;
        }

        public org.hibernate.SessionFactory getSessionFactory() {
            return this.sessionFactory;
        }

        @Override
        public synchronized void close() {
            if ( !this.open ) {
                LOG.warn( "SessionFactory aready closed" );
            }
            if ( this.sessionFactory != null ) {
                this.sessionFactory.close();
                this.sessionFactory = null;
                this.open = false;
                this.status = SessionStatus.UNSET;
            }
        }

        @Override
        public String toString() {
            return java.text.MessageFormat.format( "SessionFactory (Open: {0}, Status: {1})", this.open, this.status );
        }

    }

    private static enum SessionStatus {
        UNSET, PROD, TEST;
    }

}
