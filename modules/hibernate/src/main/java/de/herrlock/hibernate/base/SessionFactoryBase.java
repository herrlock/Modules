package de.herrlock.hibernate.base;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

import org.hibernate.SessionFactory;

/**
 * @author HerrLock
 */
public abstract class SessionFactoryBase implements AutoCloseable {
    protected SessionStatus status;
    protected final String configfileName;
    protected SessionFactory sessionFactory;

    protected static Set<Class<? extends EntityObject>> getDtoClasses() {
        ServiceLoader<EntityObject> serviceLoader = ServiceLoader.load( EntityObject.class );
        Set<Class<? extends EntityObject>> classes = new HashSet<>();
        for ( EntityObject entityObject : serviceLoader ) {
            classes.add( entityObject.getClass() );
        }
        return classes;
    }

    public SessionFactoryBase( final SessionStatus status, final String configfileName ) {
        this.status = status;
        this.configfileName = configfileName;
    }

    public SessionStatus getStatus() {
        return this.status;
    }

    protected void setStatus( final SessionStatus status ) {
        this.status = status;
    }

    protected boolean open() {
        return this.status != SessionStatus.UNSET;
    }

    public SessionFactory getSessionFactory() {
        if ( this.sessionFactory == null ) {
            this.sessionFactory = buildSessionFactory();
        }
        return this.sessionFactory;
    }

    protected abstract SessionFactory buildSessionFactory();

    @Override
    public abstract void close() throws IOException;

    @Override
    public String toString() {
        return MessageFormat.format( "{0} ({1})", getClass().getSimpleName(), detailString() );
    }

    protected String detailString() {
        return MessageFormat.format( "Status: {0}", this.status );
    }

}
