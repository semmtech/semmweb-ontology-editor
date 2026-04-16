package com.semmtech.jena.readers;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.FileUtils;


/**
 * RDFReader version of Jena's own TurtleReader that also implements a
 * connection timeout option.
 */
public class TurtleReader implements RDFReader {
    protected static int timeout = 0;

    protected com.hp.hpl.jena.n3.turtle.TurtleReader reader;
    protected RDFErrorHandler errorHandler = null;

    public TurtleReader() {
        reader = new com.hp.hpl.jena.n3.turtle.TurtleReader();
    }

    public static void setConnectionTimeout(int timeoutMs) {
        timeout = timeoutMs;
    }

    @Override
    public void read(Model model, Reader r, String base) {
        reader.read(model, r, base);
    }

    @Override
    public void read(Model model, InputStream r, String base) {
        reader.read(model, r, base);
    }

    @Override
    public void read(Model model, String url) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("Accept", "text/turtle");

            if (timeout > 0) {
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
            }
            String encoding = conn.getContentEncoding();
            if (encoding == null) {
                LoggerFactory.getLogger(this.getClass()).warn("URL content is not UTF-8");
                encoding = FileUtils.encodingUTF8;
            }

            try (InputStream stream = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(stream, encoding)) {
                read(model, reader, url);
            }
        }
        catch (JenaException e) {
            if (errorHandler == null) {
                throw e;
            }
            errorHandler.error(e);
        }
        catch (Exception ex) {
            if (errorHandler == null) {
                throw new JenaException(ex);
            }
            errorHandler.error(ex);
        }
    }

    @Override
    public Object setProperty(String propName, Object propValue) {
        return reader.setProperty(propName, propValue);
    }

    @Override
    public RDFErrorHandler setErrorHandler(RDFErrorHandler errHandler) {
        errorHandler = reader.setErrorHandler(errHandler);
        return errorHandler;
    }
}
