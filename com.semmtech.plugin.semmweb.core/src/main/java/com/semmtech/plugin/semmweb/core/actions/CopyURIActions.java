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

package com.semmtech.plugin.semmweb.core.actions;


import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;

import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.actions.runconditions.OntologyRunCondition;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.operations.ModelOperation;
import com.semmtech.ui.plugin.util.ClipboardUtils;


/**
 * Copy the URI of the ontology of the selected model/import into Clipboard.
 * This actions can be performed only if the selected model has only one
 * Ontology
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */

public class CopyURIActions {

    private CopyURIActions() {

    }

    /**
     * This action is executed if an instance of IModel is selected
     */
    public static class CopyModelURIAction extends ModelFileAction {

        public CopyModelURIAction(IModel model) {
            super((IFile) model.getResource(), false, false);
            setText("Copy URI");
            setImageDescriptor(CorePlugin.getDefault()
                    .getImageDescriptor(CorePluginImages.IMG_COPY));
            setToolTipText("Copy the Ontology URI of the selected Model");
            addRunCondition(new OntologyRunCondition());
        }

        @Override
        protected ModelOperation getOperation(OntModel model) {
            String uri = OntModelUtils.getURI(model);
            ClipboardUtils.copyText(uri != null ? uri : new String());
            return null;
        }
    }

    /**
     * This action is executed if an instance of IImport is selected
     */
    public static class CopyImportURIAction extends Action {

        private IImport immport;

        public CopyImportURIAction(IImport immport) {
            this.immport = immport;
            setText("Copy URI");
            setImageDescriptor(CorePlugin.getDefault()
                    .getImageDescriptor(CorePluginImages.IMG_COPY));
            setToolTipText("Copy the Ontology URI of the selected Model");
        }

        @Override
        public void run() {
            ClipboardUtils.copyText(immport.getURI());
        }
    }
}
