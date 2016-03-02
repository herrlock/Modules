package de.herrlock.liquibase;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.herrlock.hibernate.base.SessionFactoryBase;
import de.herrlock.hibernate.base.SessionStatus;
import liquibase.exception.LiquibaseException;

/**
 * 
 * @author HerrLock
 */
public class LiquibaseSessionFactory extends SessionFactoryBase {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final AtomicBoolean DATABASE_INITIALIZED = new AtomicBoolean( false );
    private boolean open = true;

    LiquibaseSessionFactory( final SessionStatus status, final String configfileName ) {
        super( status, configfileName );
    }

    @Override
    protected SessionFactory buildSessionFactory() {
        boolean isInitialized = DATABASE_INITIALIZED.get();
        {
            LOGGER.info( "creating sessionFactory" );
            Configuration configuration = new Configuration();

            // load dto-classes
            List<String> dtoClasses = LiquibaseSessionFactoryWrapper.getDtoClasses();
            for ( String dtoClassName : dtoClasses ) {
                LOGGER.info( "Add annotated class: {}", dtoClassName );
                try {
                    Class<?> dtoClass = Class.forName( dtoClassName );
                    configuration.addAnnotatedClass( dtoClass );
                } catch ( ClassNotFoundException ex ) {
                    LOGGER.error( new ParameterizedMessage( "Could not find class {}", dtoClasses ), ex );
                }
            }

            // complete the configuration with the given file
            configuration.configure( this.configfileName );

            this.sessionFactory = configuration.buildSessionFactory();
        }
        if ( !isInitialized ) {
            initializeDatabase( this.sessionFactory.openSession(), this.status.name() );
        }
        return this.sessionFactory;
    }

    private static void initializeDatabase( Session session, String... contexts ) {
        LOGGER.debug( "initializing database in session {}", session );
        LiquibaseException error = session.doReturningWork( new DatabaseUpdateWork( session, contexts ) );
        if ( error == null ) {
            DATABASE_INITIALIZED.set( true );
        } else {
            throw new DatabaseInitializationException( error );
        }
    }

    @Override
    public synchronized void close() {
        if ( !this.open ) {
            LOGGER.warn( "SessionFactory aready closed" );
        }
        if ( this.sessionFactory != null ) {
            this.sessionFactory.close();
            this.sessionFactory = null;
            this.open = false;
            this.status = SessionStatus.UNSET;
            DATABASE_INITIALIZED.set( false );
        }
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "SessionFactory (Open: {0}, Status: {1})", this.open, this.status );
    }

}
