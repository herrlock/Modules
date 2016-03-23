package de.herrlock.httputils;

import java.io.IOException;
import java.util.Objects;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

/**
 * A class containing a {@link HttpClient} to execute HTTP-requests. The client used by default is the created by
 * {@link HttpClients#createDefault()}. A custom client can be set by calling {@link #setClient(HttpClient)}
 * 
 * @author HerrLock
 * @since 1.0.0
 */
public final class HttpExecutor {
    private static HttpClient defaultClient = HttpClients.createDefault();

    /**
     * delegate to {@link HttpClient#execute(HttpUriRequest)}
     */
    public static HttpResponse execute( final HttpUriRequest request ) throws IOException, ClientProtocolException {
        return defaultClient.execute( request );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpUriRequest, HttpContext)}
     */
    public static HttpResponse execute( final HttpUriRequest request, final HttpContext context )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( request, context );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpHost, HttpRequest)}
     */
    public static HttpResponse execute( final HttpHost target, final HttpRequest request )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpHost, HttpRequest, HttpContext)}
     */
    public static HttpResponse execute( final HttpHost target, final HttpRequest request, final HttpContext context )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request, context );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpUriRequest, ResponseHandler)}
     */
    public static <T> T execute( final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( request, responseHandler );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpUriRequest, ResponseHandler, HttpContext)}
     */
    public static <T> T execute( final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler,
        final HttpContext context ) throws IOException, ClientProtocolException {
        return defaultClient.execute( request, responseHandler, context );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpHost, HttpRequest, ResponseHandler)}
     */
    public static <T> T execute( final HttpHost target, final HttpRequest request,
        final ResponseHandler<? extends T> responseHandler ) throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request, responseHandler );
    }

    /**
     * delegate to {@link HttpClient#execute(HttpHost, HttpRequest, ResponseHandler)}
     */
    public static <T> T execute( final HttpHost target, final HttpRequest request,
        final ResponseHandler<? extends T> responseHandler, final HttpContext context )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request, responseHandler, context );
    }

    /**
     * Set a new {@link HttpClient} for this class to use.
     * 
     * @param newClient
     *            The HttpClient to use from now on. Must not be {@code null}
     */
    public static void setClient( final HttpClient newClient ) {
        defaultClient = Objects.requireNonNull( newClient );
    }

    /**
     * not to be used
     */
    private HttpExecutor() {
        // do nothing
    }
}
