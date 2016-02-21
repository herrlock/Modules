package de.herrlock.liquibase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SomeObject {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;
    private String name;
    private String type;

    public long getId() {
        return this.id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "TestObject: {0} | {1}", this.id, this.name );
    }

}
