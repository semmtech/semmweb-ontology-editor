package com.semmtech.jena.readers;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.JenaException;


/**
 * RDFReader version of Jena's own NTripleReader that also implements a
 * connection timeout option.
 */
public class NTripleReader extends com.hp.hpl.jena.rdf.model.impl.NTripleReaderSubclassable {
    protected static int timeout = 0;

    public NTripleReader() {
        super();
    }

    public static void setConnectionTimeout(int timeoutMs) {
    	timeout = timeoutMs;
    }

    @Override
    public void read(Model model, String url) {
        try {
            URL urlObject = new URL(url);
            URLConnection conn = urlObject.openConnection();

            if (timeout > 0) {
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
            }
            
            // Close the stream, Jena implementations doen't close the readers
            // stream
            try (InputStream stream = conn.getInputStream();
                    InputStreamReader reader = new InputStreamReader(stream)) {
                read(model, reader, url);
            }
        }
        catch (Exception e) {
            throw new JenaException(e);
        }
    }
}
