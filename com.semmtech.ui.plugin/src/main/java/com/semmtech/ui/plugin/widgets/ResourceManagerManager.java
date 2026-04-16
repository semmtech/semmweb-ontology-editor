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

package com.semmtech.ui.plugin.widgets;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Maps;


/**
 * 
 * @author Sander Stolk
 */
public class ResourceManagerManager {

    private HashMap<Display, LocalResourceManager> resourceManagers;

    public LocalResourceManager getResourceManager(Display display) {
        if (resourceManagers == null) {
            resourceManagers = Maps.newHashMap();
        }

        LocalResourceManager resources = resourceManagers.get(display);
        if (resources == null) {
            pruneResourceManagers();
            resources = new LocalResourceManager(JFaceResources.getResources(display));
            resourceManagers.put(display, resources);
        }
        return resources;
    }

    private void pruneResourceManagers() {
        Set<Display> displays = resourceManagers.keySet();
        for (Iterator<Display> iter = displays.iterator(); iter.hasNext();) {
            Display display = iter.next();
            if (display.isDisposed()) {
                resourceManagers.remove(display);
                iter = displays.iterator();
            }
        }
    }
}
