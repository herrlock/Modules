package de.herrlock.jersey.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.herrlock.jersey.objects.ValueObject;

public class TestRandomNumberServlet {

    private RandomNumberService servlet = new RandomNumberService();

    @Test
    public void testSuccess() {
        final List<Integer> results = new ArrayList<>( 1000 );
        // check random number 1000 times
        for ( int i = 0; i < 1000; i++ ) {
            ValueObject randomNumber = this.servlet.getRandomNumber( 30 );
            results.add( randomNumber.getValue() );
        }
        // check every element
        for ( Integer integer : results ) {
            Assert.assertTrue( "result value is too big", integer < 30 );
            Assert.assertTrue( "result value is too small", integer >= 0 );
        }
    }

    @Test
    public void testSuccessWithoutHeader() {
        final List<Integer> results = new ArrayList<>( 1000 );
        // check random number 1000 times
        for ( int i = 0; i < 1000; i++ ) {
            ValueObject randomNumber = this.servlet.getRandomNumber( null );
            results.add( randomNumber.getValue() );
        }
        // check every element
        for ( Integer integer : results ) {
            Assert.assertTrue( "result value is too big", integer < 100 );
            Assert.assertTrue( "result value is too small", integer >= 0 );
        }
    }

}
