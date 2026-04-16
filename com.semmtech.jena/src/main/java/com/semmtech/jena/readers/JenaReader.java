package com.semmtech.jena.readers;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpHeaders;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.hp.hpl.jena.shared.JenaException;


/**
 * RDFReader version of Jena's own JenaReader that also implements a connection
 * timeout option.
 */
public class JenaReader extends com.hp.hpl.jena.rdf.arp.JenaReader {
    protected static int timeout = 0;

    public JenaReader() {
        super();
    }

    public static void setConnectionTimeout(int timeoutMs) {
        timeout = timeoutMs;
    }

    @Override
    public void read(Model model, String url) throws JenaException {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty(
                    HttpHeaders.ACCEPT,
                    "application/rdf+xml, application/xml; q=0.8, text/xml; q=0.7, application/rss+xml; q=0.3, */*; q=0.2");

            // Probably what the RCP environment does somewhere!
            // Authenticator authenticator = new Authenticator() {
            //
            // @Override
            // protected PasswordAuthentication getPasswordAuthentication() {
            // System.out.println("Setting username and password for request to <"
            // + getRequestingURL() + ">...");
            // return new PasswordAuthentication("username",
            // "password".toCharArray());
            // }
            // };
            // Authenticator.setDefault(authenticator);

            // TEMP: Create a robust solution to credentials resolution
            // String authorization = String.format("Basic %s", new
            // String(BaseEncoding.base64()
            // .encode("username:password".getBytes())));
            // conn.setRequestProperty(HttpHeaders.AUTHORIZATION,
            // authorization);

            if (timeout > 0) {
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
            }

            // if (encoding == null) {
            // encoding = conn.getContentType();

            // TODO should be parsed -> "text/html; charset=utf-8"
            // Charset charset = Charset.forName(encoding);
            // ContentType.create(encoding);
            // }

            try (InputStream stream = conn.getInputStream()) {
                String encoding = conn.getContentEncoding();
                if (encoding == null) {
                    read(model, stream, url);
                }
                else {
                    try (InputStreamReader reader = new InputStreamReader(stream, "UTF-8")) {
                        read(model, reader, url);
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            throw new DoesNotExistException(url);
        }
        catch (IOException e) {
            throw new JenaException(e);
        }
    }
}
