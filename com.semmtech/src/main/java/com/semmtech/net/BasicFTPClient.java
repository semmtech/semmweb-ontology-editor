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

package com.semmtech.net;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;


/**
 * The Class BasicFTPClient.
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class BasicFTPClient {

    /** The Constant BUFFER_SIZE. */
    private static final int BUFFER_SIZE = 1024;

    /** The connection. */
    private URLConnection connection;

    /** The host. */
    private String host;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The remote filename. */
    private String remoteFilename;

    /** The error message. */
    private String errorMessage;

    /** The success message. */
    private String successMessage;

    /**
     * Instantiates a new basic ftp client.
     */
    public BasicFTPClient() {
    }

    /**
     * Gets the host.
     * 
     * @return the host
     */
    public final String getHost() {
        return host;
    }

    /**
     * Sets the host.
     * 
     * @param host
     *            the new host
     */
    public final void setHost(final String host) {
        this.host = host;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * 
     * @param username
     *            the new username
     */
    public final void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public final void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Gets the remote filename.
     * 
     * @return the remote filename
     */
    public final String getRemoteFilename() {
        return remoteFilename;
    }

    /**
     * Sets the remote filename.
     * 
     * @param remoteFilename
     *            the new remote filename
     */
    public final void setRemoteFilename(final String remoteFilename) {
        this.remoteFilename = remoteFilename;
    }

    /**
     * Gets the error message.
     * 
     * @return the error message
     */
    public final synchronized String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the success message.
     * 
     * @return the success message
     */
    public final synchronized String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Connect.
     * 
     * @return true, if successful
     */
    public final synchronized boolean connect() {
        try {
            URL url = new URL(String.format("ftp://%s:%s@%s/%s", username, password, host,
                    remoteFilename));
            connection = url.openConnection();
            return true;
        }
        catch (Exception ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter, true);
            ex.printStackTrace(writer);
            errorMessage = stringWriter.getBuffer().toString();
        }
        return false;
    }

    /**
     * Download file.
     * 
     * @param localFilename
     *            the local filename
     * @return true, if successful
     */
    public final synchronized boolean downloadFile(final String localFilename) {
        try {
            try (InputStream inputStream = connection.getInputStream();
                    BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
                    OutputStream outputStream = new FileOutputStream(localFilename);
                    BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream)) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int readCount;
                while ((readCount = bufferedInput.read(buffer)) > 0) {
                    bufferedOutput.write(buffer, 0, readCount);
                }
                bufferedOutput.close();
                inputStream.close();
                successMessage = "File successfuly Downloaded!";

                return true;
            }
        }
        catch (Exception ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter, true);
            ex.printStackTrace(writer);
            errorMessage = stringWriter.getBuffer().toString();
        }
        return false;
    }

    /**
     * Upload file.
     * 
     * @param localFilename
     *            the local filename
     * @return true, if successful
     */
    public final synchronized boolean uploadFile(final String localFilename) {
        try {
            try (InputStream inputStream = new FileInputStream(localFilename);
                    BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
                    OutputStream outputStream = connection.getOutputStream();
                    BufferedOutputStream bufferedOutput = new BufferedOutputStream(outputStream)) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int readCount;
                while ((readCount = bufferedInput.read(buffer)) > 0) {
                    bufferedOutput.write(buffer, 0, readCount);
                }
            }

            successMessage = "File successfuly uploaded!";
            return true;
        }
        catch (Exception ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter, true);
            ex.printStackTrace(writer);
            errorMessage = stringWriter.getBuffer().toString();
        }
        return false;
    }
}
