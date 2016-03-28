package de.herrlock.hibernate.base;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;

/**
 * @author HerrLock
 *
 */
public abstract class SessionFactoryWrapper implements AutoCloseable {

    private static final Logger LOG = LogManager.getLogger();

    private SessionFactoryBase sessionFactory;

    /**
     * Create an actual Session and return it
     * 
     * @return A {@link Session} to use for interaction with a database
     */
    public abstract Session getSession();
    /**
     * Create an actual Session to test with and return it
     * 
     * @return A {@link Session} to use for interaction with a testdatabase
     */
    public abstract Session getTestSession();

    /**
     * 
     * Creates a Session from the hibernate-configuration-fie at the given location. First {@link System#getProperty(String)} is
     * use to get the property given in the first parameter. If the property is set it is used, otherwise the second parameter is
     * used.
     * 
     * @param prodSessionFileProperty
     *            the system-property-key to look up
     * @param prodSessionDefaultFile
     *            the default-location to search at
     * 
     * @return a session
     */
    protected Session getSession( final String prodSessionFileProperty, final String prodSessionDefaultFile ) {
        LOG.entry( prodSessionFileProperty, prodSessionDefaultFile );
        final String prodFileLocation = System.getProperty( prodSessionFileProperty, prodSessionDefaultFile );
        LOG.debug( "Using {} as hibernate-configuration", prodFileLocation );
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.PROD, prodFileLocation ).withOptions();
        return sessionBuilder.openSession();
    }

    /**
     * Creates a Test-Session from the hibernate-configuration-fie at the given location. First {@link System#getProperty(String)}
     * is use to get the property given in the first parameter. If the property is set it is used, otherwise the second parameter
     * is used.
     * 
     * @param testSessionFileProperty
     *            the system-property-key to look up
     * @param testSessionDefaultFile
     *            the default-location to search at
     * 
     * @return a test-session
     */
    protected Session getTestSession( final String testSessionFileProperty, final String testSessionDefaultFile ) {
        LOG.entry( testSessionFileProperty, testSessionDefaultFile );
        final String testFileLocation = System.getProperty( testSessionFileProperty, testSessionDefaultFile );
        LOG.debug( "Using {} as hibernate-configuration", testFileLocation );
        SessionBuilder sessionBuilder = getSessionFactory( SessionStatus.TEST, testFileLocation ).withOptions();
        return sessionBuilder.openSession();
    }

    /**
     * Returns a SessionFactory. If none is currently in use a new one with the requested SessionStatus is created. Otherwise the
     * current existing is returned no matter what the requested SessionStatus is.
     * 
     * @param status
     *            the requested SessionStatus for the new SessionFactory
     * @param configfileName
     *            the location of the configuration-file to initialise the SessionFactory with
     * @return a SessionFactory
     */
    private SessionFactory getSessionFactory( final SessionStatus status, final String configfileName ) {
        synchronized ( SessionFactoryWrapper.class ) {
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
    }

    /**
     * returns an actual instance of the implementation
     * 
     * @param status
     *            the requested SessionStatus
     * @param configfileName
     *            the configuration-file to use
     * @return a new SessionFactoryBase
     */
    protected abstract SessionFactoryBase createSessionFactoryBase( final SessionStatus status, final String configfileName );

    /**
     * Closes the SessionFactory-implementation by calling {@link SessionFactoryBase#close()} and unserts the current reference to
     * that instance
     */
    @Override
    public void close() throws IOException {
        if ( this.sessionFactory != null ) {
            this.sessionFactory.close();
            this.sessionFactory = null;
        }
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "SessionFactoryWrapper (Factory: {0})", this.sessionFactory );
    }

}
