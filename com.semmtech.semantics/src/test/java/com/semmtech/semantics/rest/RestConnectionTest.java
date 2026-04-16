package com.semmtech.semantics.rest;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.util.FileUtils;


public class RestConnectionTest {
    public static void main(String[] args) {
        String url = "http://repo.semmweb.com/ns/pizza/20050418/Soho";
        ModelMaker maker = ModelFactory.createMemModelMaker();
        Model model = maker.getModel(url, new ModelReader() {

            @Override
            public Model readModel(Model toRead, String url) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) (new URL(url))
                            .openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "text/turtle");

                    if (connection.getResponseCode() != 200) {
                        throw new RuntimeException("Failed: HTTP error code : "
                                + connection.getResponseCode());
                    }
                    toRead.read(connection.getInputStream(), null, FileUtils.langTurtle);

                    // BufferedReader reader = new BufferedReader(new
                    // InputStreamReader(()));
                    // String output;
                    // System.out.println("Output from Server...\n");
                    // while ((output = reader.readLine()) != null) {
                    // System.out.println(output);
                    // }

                    connection.disconnect();
                }
                catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
                return toRead;
            }
        });

        model.write(System.out, FileUtils.langTurtle, null);
    }
}
