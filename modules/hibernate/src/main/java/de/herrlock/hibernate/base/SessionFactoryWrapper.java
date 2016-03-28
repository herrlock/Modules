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

    public abstract Session getSession();
    public abstract Session getTestSession();

    /**
     * Creates a Session.
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
     * Creates a Test-Session
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

    protected abstract SessionFactoryBase createSessionFactoryBase( final SessionStatus status, final String configfileName );

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
