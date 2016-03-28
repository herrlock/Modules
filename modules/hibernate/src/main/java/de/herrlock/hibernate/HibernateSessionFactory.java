package de.herrlock.hibernate;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import de.herrlock.hibernate.base.EntityObject;
import de.herrlock.hibernate.base.SessionFactoryBase;
import de.herrlock.hibernate.base.SessionStatus;

/**
 * 
 * @author HerrLock
 */
public final class HibernateSessionFactory extends SessionFactoryBase {
    private static final Logger LOG = LogManager.getLogger();

    HibernateSessionFactory( final SessionStatus status, final String configfileName ) {
        super( status, configfileName );
    }

    @Override
    protected SessionFactory buildSessionFactory() {
        if ( this.sessionFactory == null ) {
            LOG.info( "creating sessionFactory" );
            Configuration configuration = new Configuration();

            // load dto-classes
            Set<Class<? extends EntityObject>> dtoClasses = getDtoClasses();
            for ( Class<? extends EntityObject> dtoClass : dtoClasses ) {
                LOG.info( "Add annotated class: {}", dtoClass );
                configuration.addAnnotatedClass( dtoClass );
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
            if ( !open() ) {
                LOG.warn( "SessionFactory aready closed" );
            }
            if ( this.sessionFactory != null ) {
                this.sessionFactory.close();
                this.sessionFactory = null;
                setStatus( SessionStatus.UNSET );
            }
        }
    }

}
