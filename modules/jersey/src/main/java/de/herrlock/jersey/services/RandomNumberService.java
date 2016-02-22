package de.herrlock.jersey.services;

import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.herrlock.jersey.objects.ValueObject;

/**
 * Simple Service that returns a random number between 0 and 100, or - if the http-header "Max-Random" is given - between 0 and
 * the given number
 * 
 * @author HerrLock
 */
@Path( "/randomnumber" )
public class RandomNumberService {

    private final Random random = new Random();

    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public ValueObject getRandomNumber( @HeaderParam( value = "Max-Random" ) Integer maxValue) {
        ValueObject valueObject = new ValueObject();
        int max = maxValue == null ? 100 : maxValue;
        int nextInt = this.random.nextInt( max );
        valueObject.setValue( nextInt );
        return valueObject;
    }
}
