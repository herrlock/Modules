package de.herrlock.hibernate.base;

import org.hibernate.SessionFactory;

/**
 * @author HerrLock
 */
public abstract class SessionFactoryBase implements AutoCloseable {
    protected SessionStatus status;
    protected final String configfileName;
    protected SessionFactory sessionFactory;

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

    public SessionFactory getSessionFactory() {
        if ( this.sessionFactory == null ) {
            this.sessionFactory = buildSessionFactory();
        }
        return this.sessionFactory;
    }

    protected abstract SessionFactory buildSessionFactory();

}
