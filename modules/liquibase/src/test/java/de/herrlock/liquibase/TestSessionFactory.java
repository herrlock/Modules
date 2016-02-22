package de.herrlock.liquibase;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSessionFactory {

    @BeforeClass
    public static void initDB() {
        try ( Session session = SessionFactory.getTestSession() ) {
            try {
                Transaction transaction = session.beginTransaction();

                SomeObject o1 = new SomeObject();
                o1.setName( "o1" );
                o1.setType( "red" );
                session.save( o1 );

                SomeObject o2 = new SomeObject();
                o2.setName( "o2" );
                o2.setType( "red" );
                session.save( o2 );

                SomeObject o3 = new SomeObject();
                o3.setName( "o3" );
                o3.setType( "green" );
                session.save( o3 );

                transaction.commit();
            } catch ( RuntimeException ex ) {
                session.getTransaction().rollback();
            }
        }
    }

    @AfterClass
    public static void after() {
        SessionFactory.close();
    }

    @Test
    public void testCount() {
        try ( Session session = SessionFactory.getTestSession() ) {
            // select all SomeObject-instances
            List<?> list = session.createCriteria( SomeObject.class ).list();
            Assert.assertEquals( 3, list.size() );
        }
    }

    @Test
    public void testType() {
        try ( Session session = SessionFactory.getTestSession() ) {
            // select all SomeObject-instances with type == red
            List<?> list = session.createCriteria( SomeObject.class ).add( Restrictions.eq( "type", "red" ) ).list();
            Assert.assertEquals( 2, list.size() );
        }
    }

}
