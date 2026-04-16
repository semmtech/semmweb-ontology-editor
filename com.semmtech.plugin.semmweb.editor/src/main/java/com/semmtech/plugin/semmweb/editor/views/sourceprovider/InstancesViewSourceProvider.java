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

package com.semmtech.plugin.semmweb.editor.views.sourceprovider;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;


/**
 * This provider determine whether the Group By Direct Type in the Instances
 * View is pressed or not. This information will be used to hide the Collapse
 * All button in the instances view toolbar when a table view is shown instead
 * of a tree view
 * 
 * @author Simone Rondelli
 */
public class InstancesViewSourceProvider extends AbstractSourceProvider {

    public static final String GROUP_BY_DIRECT_TYPE = "com.semmtech.plugin.semmweb.editor.views.sourceprovider.groupByDirectType";

    public static final String ENABLED = "ENABLED";

    public static final String DISABLED = "DISABLED";

    private boolean groupByDirectType;

    public InstancesViewSourceProvider() {
        groupByDirectType = false;
    }

    @Override
    public void dispose() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState() {
        Map<String, String> states = new HashMap<>(1);
        states.put(GROUP_BY_DIRECT_TYPE, groupByDirectType ? ENABLED : DISABLED);
        return states;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { GROUP_BY_DIRECT_TYPE };
    }

    private void setGroupByType(boolean enabled) {
        if (enabled == groupByDirectType) {
            return;
        }

        this.groupByDirectType = enabled;
        fireSourceChanged(ISources.WORKBENCH, GROUP_BY_DIRECT_TYPE, groupByDirectType ? ENABLED
                : DISABLED);
    }

    public static void setGroupByType(IWorkbenchWindow window, boolean enabled) {
        ISourceProviderService sourceProviderService = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
        InstancesViewSourceProvider instancesViewSourceProvider = (InstancesViewSourceProvider) sourceProviderService
                .getSourceProvider(InstancesViewSourceProvider.GROUP_BY_DIRECT_TYPE);

        instancesViewSourceProvider.setGroupByType(enabled);
    }

}
