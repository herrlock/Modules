package de.herrlock.hibernate;

import java.io.IOException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSessionFactoryWrapper {

    public static final HibernateSessionFactoryWrapper SFW = HibernateSessionFactoryWrapper.SFW;

    /**
     * open a test-session and add three objects to the db
     */
    @Before
    public void initDB() {
        try ( Session session = SFW.getTestSession() ) {
            Transaction transaction = session.beginTransaction();
            try {
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
            } catch ( HibernateException ex ) {
                transaction.rollback();
            }
        }
    }

    /**
     * Clean the session-factory
     * 
     * @throws Exception
     */
    @After
    public void after() throws IOException {
        SFW.close();
    }

    /**
     * select all objects
     */
    @Test
    public void testCount() {
        try ( Session session = SFW.getTestSession() ) {
            // select all SomeObject-instances
            List<?> list = session.createCriteria( SomeObject.class ).list();
            Assert.assertEquals( "Should contain 3 objects", 3, list.size() );
        }
    }

    /**
     * select all objects that have a "type" of "red"
     */
    @Test
    public void testType() {
        try ( Session session = SFW.getTestSession() ) {
            // select all SomeObject-instances with type == red
            List<?> list = session.createCriteria( SomeObject.class ).add( Restrictions.eq( "type", "red" ) ).list();
            Assert.assertEquals( "Should contain 2 objects with type=red", 2, list.size() );
        }
    }

    /**
     * select all objects, close the session-factory and use a new one
     * 
     * @throws Exception
     */
    @Test
    public void multiSession() throws IOException {
        try ( Session session = SFW.getTestSession() ) {
            // select all SomeObject-instances
            List<?> list = session.createCriteria( SomeObject.class ).list();
            Assert.assertEquals( "Should contain 3 objects", 3, list.size() );
        }
        SFW.close();
        try ( Session session = SFW.getTestSession() ) {
            // select all SomeObject-instances
            List<?> list = session.createCriteria( SomeObject.class ).list();
            Assert.assertEquals( "Should contain no objects now", 0, list.size() );
        }
    }

}
