package de.herrlock.liquibase;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.ReturningWork;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class DatabaseUpdateWork implements ReturningWork<LiquibaseException> {
    private static final Logger LOG = LogManager.getLogger();

    public static final String CHANGE_LOG_LOCATION_PROPERTY = "de.herrlock.liquibase.changelog";
    private static final String CHANGE_LOG_FILE = "de/herrlock/liquibase/db.changelog-master.xml";

    private final Session session;
    private final String[] contexts;

    public DatabaseUpdateWork( Session session, String... contexts ) {
        this.session = session;
        this.contexts = contexts;
    }

    @Override
    public LiquibaseException execute( Connection connection ) throws SQLException {
        Transaction transaction = null;
        try {
            ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor( DatabaseUpdateWork.class.getClassLoader() );
            DatabaseConnection databaseConnection = new JdbcConnection( connection );
            Liquibase liquibase = new Liquibase( CHANGE_LOG_FILE, resourceAccessor, databaseConnection );

            transaction = this.session.beginTransaction();
            liquibase.update( new Contexts( this.contexts ) );
            transaction.commit();
            return null;
        } catch ( LiquibaseException e ) {
            LOG.error( e );
            if ( transaction != null ) {
                transaction.rollback();
            }
            return e;
        }

    }
}
