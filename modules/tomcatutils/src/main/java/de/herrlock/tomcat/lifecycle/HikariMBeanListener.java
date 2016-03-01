package de.herrlock.tomcat.lifecycle;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class HikariMBeanListener implements LifecycleListener {
    private static final Logger LOG = LogManager.getLogger();

    private final Timer timer = new Timer( true );

    @Override
    public void lifecycleEvent( LifecycleEvent event ) {
        String type = event.getType();
        if ( "start".equals( type ) ) {
            start();
        } else if ( "stop".equals( type ) ) {
            stop();
        }
    }

    private void start() {
        LOG.entry();
        try {
            Class.forName( "com.zaxxer.hikari.pool.HikariPoolMBean" );
            this.timer.scheduleAtFixedRate( new HikarPoolLogger(), 60_000, 60_000 );
        } catch ( ClassNotFoundException ex ) {
            // HikariPoolMBean not in classpath, so terminate without adding the task
            LOG.error( "HikariPoolMBean not found, ignoring this Timer", ex );
        }
    }

    private void stop() {
        LOG.entry();
        this.timer.cancel();
    }

    private static final class HikarPoolLogger extends TimerTask {
        private static final Logger LOGGER = LogManager.getLogger();

        private static final Class<?> HIKARI_POOL_MBEAN_CLASS;
        private static final Method GET_TOTAL_CONNECTIONS, GET_IDLE_CONNECTIONS, GET_ACTIVE_CONNECTIONS,
            GET_THREADS_AWAITING_CONNECTION;

        static {
            try {
                HIKARI_POOL_MBEAN_CLASS = Class.forName( "com.zaxxer.hikari.pool.HikariPoolMBean" );
                GET_TOTAL_CONNECTIONS = HIKARI_POOL_MBEAN_CLASS.getMethod( "getTotalConnections" );
                GET_IDLE_CONNECTIONS = HIKARI_POOL_MBEAN_CLASS.getMethod( "getIdleConnections" );
                GET_ACTIVE_CONNECTIONS = HIKARI_POOL_MBEAN_CLASS.getMethod( "getActiveConnections" );
                GET_THREADS_AWAITING_CONNECTION = HIKARI_POOL_MBEAN_CLASS.getMethod( "getThreadsAwaitingConnection" );
            } catch ( ReflectiveOperationException ex ) {
                throw new RuntimeException( ex );
            }
        }

        @Override
        public void run() {
            try {
                MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                ObjectName poolName = new ObjectName( "com.zaxxer.hikari:type=Pool (HikariPool-0)" );
                Object poolProxy = JMX.newMBeanProxy( mBeanServer, poolName, HIKARI_POOL_MBEAN_CLASS );

                Object totalConnections = GET_TOTAL_CONNECTIONS.invoke( poolProxy );
                Object idleConnections = GET_IDLE_CONNECTIONS.invoke( poolProxy );
                Object activeConnections = GET_ACTIVE_CONNECTIONS.invoke( poolProxy );
                Object threadsAwaitingConnection = GET_THREADS_AWAITING_CONNECTION.invoke( poolProxy );

                LOGGER.info( "Total: " + totalConnections + ", Idle: " + idleConnections + ", Active: " + activeConnections
                    + ", Waiting: " + threadsAwaitingConnection );
            } catch ( MalformedObjectNameException | ReflectiveOperationException ex ) {
                LOGGER.warn( "Exception: " + ex + ", Cause: " + ex.getCause() );
            }
        }
    }
}
