package com.semmtech.plugin.semmweb.core;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class ReadingResourceExample {
    /**
     * Example found on
     * http://blog.vogella.com/2010/07/06/reading-resources-from-plugin/
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
        URL url;
        url = new URL("platform:/plugin/de.vogella.rcp.plugin.filereader/files/test.txt");
        try (InputStream inputStream = url.openConnection().getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
        }
    }
}
