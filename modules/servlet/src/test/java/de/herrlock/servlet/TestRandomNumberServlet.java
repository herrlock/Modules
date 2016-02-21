package de.herrlock.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestRandomNumberServlet {

    private HttpServletRequest req;
    private HttpServletResponse res;
    private RandomNumberServlet servlet;

    @Before
    public void before() {
        this.req = Mockito.mock( HttpServletRequest.class );
        this.res = Mockito.mock( HttpServletResponse.class );
        this.servlet = new RandomNumberServlet();
    }

    @Test
    public void testSuccess() throws ServletException, IOException {
        Mockito.when( this.req.getHeader( "Max-Random" ) ).thenReturn( "30" );
        final List<String[]> results = new ArrayList<>( 1000 );
        // check random number 1000 times
        for ( int i = 0; i < 1000; i++ ) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            Mockito.when( this.res.getWriter() ).thenReturn( new PrintWriter( buf, true ) );
            this.servlet.doGet( this.req, this.res );
            results.add( buf.toString().split( "\\r?\\n" ) );
        }
        // check every element
        for ( String[] strings : results ) {
            Assert.assertEquals( "result value has too many lines", 1, strings.length );
            Assert.assertTrue( "result value is too small", Integer.parseInt( strings[0] ) < 30 );
            Assert.assertTrue( "result value is too big", Integer.parseInt( strings[0] ) >= 0 );
        }
    }

    @Test
    public void testFailure() throws ServletException, IOException {
        Mockito.when( this.req.getHeader( "Max-Random" ) ).thenReturn( "NaN" );
        this.servlet.doGet( this.req, this.res );
        // assert the status is set to 400 exactly once
        Mockito.inOrder( this.res ).verify( this.res, Mockito.calls( 1 ) ).setStatus( 400 );
    }

}
