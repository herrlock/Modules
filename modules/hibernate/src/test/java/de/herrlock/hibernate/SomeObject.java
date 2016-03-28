package de.herrlock.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.auto.service.AutoService;

import de.herrlock.hibernate.base.EntityObject;

@AutoService( EntityObject.class )
@Entity
public class SomeObject implements EntityObject {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long id;
    private String name;
    private String type;

    public long getId() {
        return this.id;
    }

    public void setId( final long id ) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType( final String type ) {
        this.type = type;
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "TestObject: {0} | {1}", this.id, this.name );
    }

}
