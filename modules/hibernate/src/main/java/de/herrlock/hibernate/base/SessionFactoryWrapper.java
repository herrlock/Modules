package de.herrlock.hibernate.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;

import de.herrlock.hibernate.HibernateSessionFactoryWrapper;

/**
 * @author HerrLock
 *
 */
public abstract class SessionFactoryWrapper implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger();

    private SessionFactoryBase sessionFactory;

    public abstract Session getSession();
    public abstract Session getTestSession();

    /**
     * Creates a Session from the configuration-file {@value PROD_SESSION_FILE}
     * 
     * @param prodSessionFileProperty
     * @param prodSessionDefaultFile
     * 
     * @return a session
     */
    protected Session getSession( final String prodSessionFileProperty, final String prodSessionDefaultFile ) {
        final String prodFileLocation = System.getProperty( prodSessionFileProperty, prodSessionDefaultFile );
        LOG.debug( "Using {} as hibernate-configuration", prodFileLocation );
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.PROD, prodFileLocation ).withOptions();
        return sessionBuilder.openSession();
    }

    /**
     * Creates a Session from the configuration-file {@value TEST_SESSION_FILE}
     * 
     * @return a test-session
     */
    public Session getTestSession( final String testSessionFileProperty, final String testSessionDefaultFile ) {
        final String testFileLocation = System.getProperty( testSessionFileProperty, testSessionDefaultFile );
        LOG.debug( "Using {} as hibernate-configuration", testFileLocation );
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.TEST, testFileLocation ).withOptions();
        return sessionBuilder.openSession();
    }

    private synchronized SessionFactory getSessionFactory( final SessionStatus status, final String configfileName ) {
        LOG.entry( configfileName );
        if ( this.sessionFactory == null ) {
            this.sessionFactory = createSessionFactoryBase( status, configfileName );
        }
        if ( this.sessionFactory.getStatus() != status ) {
            LOG.warn( "returning SessionFactory with SessionStatus {} but requested was {}", this.sessionFactory.getStatus(),
                status );
        }
        return this.sessionFactory.getSessionFactory();
    }

    protected abstract SessionFactoryBase createSessionFactoryBase( final SessionStatus status, final String configfileName );

    @Override
    public void close() throws Exception {
        if ( this.sessionFactory != null ) {
            this.sessionFactory.close();
            this.sessionFactory = null;
        }
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "SessionFactoryWrapper (Factory: {0})", this.sessionFactory );
    }

    protected static List<String> getDtoClasses( final String classListProperty, final String classListDefaultFile ) {
        String classListLocation = System.getProperty( classListProperty );
        if ( classListLocation == null ) {
            // no custom location set, use default-location
            return loadDtoClassesFrom( HibernateSessionFactoryWrapper.class.getResource( classListDefaultFile ) );
        }
        /* classListLocation != null */
        // custom location set, use given location
        URL resource = HibernateSessionFactoryWrapper.class.getResource( classListLocation );
        if ( resource == null ) {
            // custom class-list cannot be found
            return loadDtoClassesFrom( HibernateSessionFactoryWrapper.class.getResource( classListDefaultFile ) );
        }
        /* resource != null */
        // custom class-list could be located
        return loadDtoClassesFrom( resource );
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

}
