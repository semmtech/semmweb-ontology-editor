/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.ui.plugin.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * Utility class for putting files into the system clipboard
 * 
 * @see http://blog.vogella.com/2009/09/04/swt-clipboard/
 * 
 * @author Simone Rondelli
 */
public final class ClipboardUtils {

    private ClipboardUtils() {
    }

    /**
     * Copy a file into the clipboard. Assumes the file exists -> no additional
     * check
     * 
     * @param fileName
     *            - includes the path
     */
    public static void copyFile(String fileName) {
        Clipboard clipboard = getClipboard();
        String[] data = { fileName };
        clipboard.setContents(new Object[] { data }, new Transfer[] { FileTransfer.getInstance() });
        clipboard.dispose();
    }

    /**
     * Copy the given List of files into the clipboard. Assumes the file exists
     * -> no additional check
     * 
     * @param filesName
     *            - includes the path
     */
    public static void copyFiles(List<String> fileName) {
        Clipboard clipboard = getClipboard();
        clipboard.setContents(new Object[] { fileName.toArray(new String[] {}) },
                new Transfer[] { FileTransfer.getInstance() });
        clipboard.dispose();
    }

    /**
     * Copy the given Text to the Clipboard
     */
    public static void copyText(String text) {
        Clipboard clipboard = getClipboard();
        Object[] data = { text };
        clipboard.setContents(data, new Transfer[] { TextTransfer.getInstance() });
        clipboard.dispose();
    }

    /**
     * Gets an instance of the Clipboard.
     * <p>
     * NB: Remember to dispose it.
     */
    public static Clipboard getClipboard() {
        Display display = EclipseUIPlugin.getStandardDisplay();
        return new Clipboard(display);
    }

    /**
     * Returns a list with the file paths in the clipboard
     * 
     * @return List of string or an empty List
     */
    public static List<String> getFiles() {
        Clipboard clipboard = getClipboard();

        String[] filePaths = (String[]) clipboard.getContents(FileTransfer.getInstance());
        clipboard.dispose();

        if (filePaths == null || filePaths.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(filePaths);
    }

    /**
     * Returns a String with the text content of the Clipboard
     * 
     * @return Text or null
     */
    public static String getTextFromClipboard() {
        Clipboard clipboard = getClipboard();
        String text = (String) clipboard.getContents(TextTransfer.getInstance());
        clipboard.dispose();
        return text;
    }
}