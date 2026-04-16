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

package com.semmtech.plugin.semmweb.core.resourceviewer;


import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.hp.hpl.jena.rdf.model.Resource;


/**
 * You can use this content provider together with a normal {@link TableViewer}
 * (in this case you have to remember to set manually the
 * {@link AbstractResourceViewModel} or with the {@link ResourceTableViewer}
 * that does all the "dirty work" for you
 * 
 * @author Simone Rondelli
 * 
 */
public class ResourceTableContentProvider extends AbstractResourceContentProvider implements
        ILazyContentProvider {

    private TableViewer viewer;

    @Override
    public void viewerChanged(Viewer viewer) {
        if (viewer instanceof TableViewer) {
            this.viewer = (TableViewer) viewer;
        }
    }

    @Override
    public int getItemCount() {
        if (viewModel != null) {
            return viewModel.getChildCount();
        }
        return 0;
    }

    @Override
    public void updateElement(int index) {
        if ((currentModel == null) || (viewModel == null)) {
            return;
        }

        Resource resource = viewModel.getChild(index);
        viewer.replace(resource, index);
    }

}
