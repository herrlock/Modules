package de.herrlock.servlet;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple Servlet that returns a random number between 0 and 100, or - if the http-header "Max-Random" is given - between 0 and
 * the given number
 * 
 * @author HerrLock
 */
public class RandomNumberServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Random random = new Random();

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res ) throws ServletException, IOException {
        String header = req.getHeader( "Max-Random" );
        try {
            int max = header == null ? 100 : Integer.parseInt( header );
            int nextInt = this.random.nextInt( max );
            res.getWriter().println( nextInt );
        } catch ( NumberFormatException ex ) {
            res.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        }
    }
}
