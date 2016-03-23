package de.herrlock.hibernate;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.herrlock.hibernate.base.SessionFactoryBase;
import de.herrlock.hibernate.base.SessionStatus;

/**
 * 
 * @author HerrLock
 */
public final class HibernateSessionFactory extends SessionFactoryBase {
    private static final Logger LOG = LogManager.getLogger();

    private boolean open = true;

    HibernateSessionFactory( final SessionStatus status, final String configfileName ) {
        super( status, configfileName );
    }

    @Override
    protected SessionFactory buildSessionFactory() {
        if ( this.sessionFactory == null ) {
            LOG.info( "creating sessionFactory" );
            Configuration configuration = new Configuration();

            // load dto-classes
            List<String> dtoClasses = HibernateSessionFactoryWrapper.getDtoClasses();
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
            configuration.configure( this.configfileName );

            this.sessionFactory = configuration.buildSessionFactory();
        }
        return this.sessionFactory;
    }

    @Override
    public void close() {
        synchronized ( HibernateSessionFactory.class ) {
            if ( !this.open ) {
                LOG.warn( "SessionFactory aready closed" );
            }
            if ( this.sessionFactory != null ) {
                this.sessionFactory.close();
                this.sessionFactory = null;
                this.open = false;
                setStatus( SessionStatus.UNSET );
            }
        }
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "SessionFactory (Open: {0}, Status: {1})", this.open, this.status );
    }

}
