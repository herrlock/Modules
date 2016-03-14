package de.herrlock.httputils;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

public final class HttpUtils {
    private static final HttpClient defaultClient = HttpClients.createDefault();

    public static HttpResponse execute( HttpUriRequest request ) throws IOException, ClientProtocolException {
        return defaultClient.execute( request );
    }

    public static HttpResponse execute( HttpUriRequest request, HttpContext context )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( request, context );
    }

    public static HttpResponse execute( HttpHost target, HttpRequest request ) throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request );
    }

    public static HttpResponse execute( HttpHost target, HttpRequest request, HttpContext context )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request, context );
    }

    public static <T> T execute( HttpUriRequest request, ResponseHandler<? extends T> responseHandler )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( request, responseHandler );
    }

    public static <T> T execute( HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( request, responseHandler, context );
    }

    public static <T> T execute( HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler )
        throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request, responseHandler );
    }

    public static <T> T execute( HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler,
        HttpContext context ) throws IOException, ClientProtocolException {
        return defaultClient.execute( target, request, responseHandler, context );
    }

}
