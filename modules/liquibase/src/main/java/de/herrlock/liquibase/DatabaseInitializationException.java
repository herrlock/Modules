package de.herrlock.liquibase;

import liquibase.exception.LiquibaseException;

public class DatabaseInitializationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DatabaseInitializationException( LiquibaseException cause ) {
        super( cause );
    }

}