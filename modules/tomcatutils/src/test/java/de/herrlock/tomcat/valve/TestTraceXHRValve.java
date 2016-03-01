package de.herrlock.tomcat.valve;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 *
 */
public class TestTraceXHRValve {

    TraceXHRValve valve = new TraceXHRValve();

    @Test
    public void testMatching() {
        this.valve.excludePattern = "/model";
        Assert.assertTrue( this.valve.checkOnMatch( "/model" ) );
    }

    @Test
    public void testPartlyMatching() {
        this.valve.excludePattern = "/openui5";
        Assert.assertTrue( this.valve.checkOnMatch( "/openui5-1.34.8/resources/sap-ui-core.js" ) );
    }

    @Test
    public void testSplitMatching() {
        this.valve.excludePattern = "/model'/test";
        Assert.assertTrue( this.valve.checkOnMatch( "/test" ) );
        Assert.assertTrue( this.valve.checkOnMatch( "/model" ) );
    }

    @Test
    public void testNotMatching() {
        Assert.assertFalse( this.valve.checkOnMatch( "/model" ) );
    }
}
