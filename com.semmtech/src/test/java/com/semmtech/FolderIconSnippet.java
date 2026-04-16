package com.semmtech;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.ini4j.Wini;


/**
 * This snippet shows how to give a folder and icon and additionally give it a
 * tooltip description.
 * 
 * @author Mike Henrichs
 * 
 */
public class FolderIconSnippet {
    private static final String DESKTOP_INI = "desktop.ini";
    private static final String ICO_PATH = "D:\\Temp\\semmtech-folder.ico";

    public static void main(String[] args) throws IOException {
        String path = "D:\\Users\\Mike Henrichs\\SEMMweb Workspace";

        File folder = new File(path);

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File parent, String name) {
                return DESKTOP_INI.equalsIgnoreCase(name);
            }
        });

        File file = null;
        if (files.length > 0) {
            file = files[0];
            Runtime.getRuntime().exec("attrib -H " + file.getAbsolutePath());
        }
        if (file == null) {
            file = new File(folder, DESKTOP_INI);
            file.createNewFile();
        }

        Wini ini = new Wini(file);
        String field = ICO_PATH + ",0";
        ini.put(".ShellClassInfo", "IconResource", field);
        ini.put(".ShellClassInfo", "InfoTip", "This is a temporary folder made by me!");
        ini.store();
        // Make desktop.ini hidden
        Runtime.getRuntime().exec("attrib +H " + file.getAbsolutePath());
        // Make the folder a system folder (otherwise desktop.ini is ignored)
        Runtime.getRuntime().exec("attrib +s " + folder.getAbsolutePath());
    }
}
