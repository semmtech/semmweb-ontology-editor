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

package com.semmtech.plugin.semmweb.core.shell;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import com.google.common.collect.Lists;


public final class ShellWin32Util {
    private ShellWin32Util() {
    }

    public static void showInExplorer(URI uri) {
        showInExplorer(uri, IResource.FILE);
    }

    public static void showInExplorer(URI uri, int type) {
        try {
            File file = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());
            IStringVariableManager manager = VariablesPlugin.getDefault()
                    .getStringVariableManager();
            String location = manager
                    .performStringSubstitution("${env_var:SystemRoot}\\explorer.exe");
            List<String> params = Lists.newArrayList();
            params.add(location);
            params.add("/select,");
            params.add(String.format("\"%s\"", file.getAbsolutePath()));
            ProcessBuilder builder = new ProcessBuilder(params);
            builder.start();
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
