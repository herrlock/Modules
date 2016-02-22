package de.herrlock.logging;

import java.util.Arrays;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger LOG = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    public static void main( final String... args ) {
        LOG.entry( Arrays.toString( args ) );

        LOG.info( "infoMessage" );
        LOG.info( "Random number between 0 and 5: {}", randomInt( 5 ) );

        LOG.exit();
    }

    public static int randomInt( int max ) {
        return RANDOM.nextInt( max );
    }
}
