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

package com.semmtech;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * This utility class is used to access the environment variables of the current
 * system (Windows only).
 * 
 * @author Mike Henrichs
 */
public final class Environment {

    /**
     * Instantiates a new environment.
     */
    private Environment() {

    }

    /**
     * Returns a set of properties which have been set as the Windows
     * environmental variables.
     * 
     * @return set of properties set as the Windows environmental variables
     * @throws IOException
     *             in case of exception
     * 
     */
    public static Properties getEnvironmentVariables() throws IOException {
        Process process = null;
        Properties variables = new Properties();
        Runtime runtime = Runtime.getRuntime();
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.indexOf("windows 9") > -1) {
            process = runtime.exec("command.com /c set");
        }
        else if ((osName.indexOf("nt") > -1) || (osName.indexOf("windows 2000") > -1)
                || (osName.indexOf("windows xp") > -1) || (osName.indexOf("windows 7") > -1)) {
            process = runtime.exec("cmd.exe /c set");
        }
        else {
            process = runtime.exec("env");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            int index = line.indexOf('=');
            String key = line.substring(0, index);
            String value = line.substring(index + 1);
            variables.setProperty(key, value);
        }

        return variables;
    }
}
