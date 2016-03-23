package de.herrlock.hibernate;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import de.herrlock.hibernate.base.SessionFactoryBase;
import de.herrlock.hibernate.base.SessionFactoryWrapper;
import de.herrlock.hibernate.base.SessionStatus;

public final class HibernateSessionFactoryWrapper extends SessionFactoryWrapper {
    private static final Logger LOG = LogManager.getLogger();

    public static final String PROD_SESSION_FILE = "hibernate.cfg.xml";
    public static final String PROD_SESSION_FILE_PROPERTY = "de.herrlock.hibernate.cfg";
    public static final String TEST_SESSION_FILE = "hibernate.test.cfg.xml";
    public static final String TEST_SESSION_FILE_PROPERTY = "de.herrlock.hibernate.test.cfg";
    public static final String CLASS_LIST_PROPERTY = "de.herrlock.hibernate.classList";
    public static final String CLASS_LIST_DEFAULT_FILE = "/de/herrlock/hibernate/classList.txt";

    public static final HibernateSessionFactoryWrapper SFW = new HibernateSessionFactoryWrapper();

    /**
     * prevent instantiation
     */
    private HibernateSessionFactoryWrapper() {
        // singleton
    }

    /**
     * Creates a Session from the configuration-file {@value PROD_SESSION_FILE}
     * 
     * @return a session
     */
    @Override
    public Session getSession() {
        LOG.entry();
        return getSession( PROD_SESSION_FILE_PROPERTY, PROD_SESSION_FILE );
    }

    /**
     * Creates a Session from the configuration-file {@value TEST_SESSION_FILE}
     * 
     * @return a test-session
     */
    @Override
    public Session getTestSession() {
        LOG.entry();
        return getTestSession( TEST_SESSION_FILE_PROPERTY, TEST_SESSION_FILE );
    }

    static List<String> getDtoClasses() {
        return getDtoClasses( CLASS_LIST_PROPERTY, CLASS_LIST_DEFAULT_FILE );
    }

    @Override
    protected SessionFactoryBase createSessionFactoryBase( final SessionStatus status, final String configfileName ) {
        return new HibernateSessionFactory( status, configfileName );
    }

}
