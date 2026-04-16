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


import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.ISourceProviderService;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;


public class SelectedURISourceProvider extends AbstractSourceProvider {

    public static final String SELECTED_URI = "com.semmtech.plugin.semmweb.editor.views.sourceprovider.selectedURI";

    private String selectedURI;

    public SelectedURISourceProvider() {
        // else if (firstElement instanceof StatementImpl) {
        // return ((StatementImpl) firstElement).getSubject();
        // }
    }

    @Override
    public void dispose() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map getCurrentState() {
        Map<String, String> state = Maps.newHashMapWithExpectedSize(1);
        state.put(SELECTED_URI, selectedURI);
        return state;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SELECTED_URI };
    }

    public void setSelectedURI(String uri) {
        if (Objects.equal(selectedURI, uri)) {
            return;
        }

        this.selectedURI = uri;
        fireSourceChanged(ISources.WORKBENCH, SELECTED_URI, uri);
    }

    public String getSelectedURI() {
        return selectedURI;
    }

    public static SelectedURISourceProvider create(IWorkbenchWindow window) {
        ISourceProviderService sourceProviderService = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
        SelectedURISourceProvider selectedURISourceProvider = (SelectedURISourceProvider) sourceProviderService
                .getSourceProvider(SELECTED_URI);
        return selectedURISourceProvider;
    }

}
