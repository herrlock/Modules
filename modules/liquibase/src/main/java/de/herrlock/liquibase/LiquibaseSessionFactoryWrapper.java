package de.herrlock.liquibase;

import java.util.List;

import org.hibernate.Session;

import de.herrlock.hibernate.base.SessionFactoryBase;
import de.herrlock.hibernate.base.SessionFactoryWrapper;
import de.herrlock.hibernate.base.SessionStatus;

public class LiquibaseSessionFactoryWrapper extends SessionFactoryWrapper {
    // private static final Logger LOG = LogManager.getLogger();

    public static final String PROD_SESSION_FILE = "hibernate.cfg.xml";
    public static final String PROD_SESSION_FILE_PROPERTY = "de.herrlock.liquibase.cfg";
    public static final String TEST_SESSION_FILE = "hibernate.test.cfg.xml";
    public static final String TEST_SESSION_FILE_PROPERTY = "de.herrlock.liquibase.test.cfg";
    public static final String CLASS_LIST_PROPERTY = "de.herrlock.liquibase.classList";
    public static final String CLASS_LIST_DEFAULT_FILE = "/de/herrlock/liquibase/classList.txt";

    public static final LiquibaseSessionFactoryWrapper SFW = new LiquibaseSessionFactoryWrapper();

    /**
     * prevent instantiation
     */
    private LiquibaseSessionFactoryWrapper() {
        // singleton
    }

    /**
     * Creates a Session from the configuration-file {@value PROD_SESSION_FILE}
     * 
     * @return a session
     */
    @Override
    public Session getSession() {
        return getSession( PROD_SESSION_FILE_PROPERTY, PROD_SESSION_FILE );
    }

    /**
     * Creates a Session from the configuration-file {@value TEST_SESSION_FILE}
     * 
     * @return a test-session
     */
    @Override
    public Session getTestSession() {
        return getTestSession( TEST_SESSION_FILE_PROPERTY, TEST_SESSION_FILE );
    }

    static List<String> getDtoClasses() {
        return getDtoClasses( CLASS_LIST_PROPERTY, CLASS_LIST_DEFAULT_FILE );
    }

    @Override
    protected SessionFactoryBase createSessionFactoryBase( final SessionStatus status, final String configfileName ) {
        return new LiquibaseSessionFactory( status, configfileName );
    }

}
