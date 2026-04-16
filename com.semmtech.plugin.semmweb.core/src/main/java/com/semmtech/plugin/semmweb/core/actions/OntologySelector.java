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


import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * Retrieve ONE ontology from the given Model.
 * 
 * The following three scenarios are possible:
 * <ol>
 * <li>No ontology has been defined in the base model. The user is prompted to
 * create a new ontology. The import will be added to that ontology.</li>
 * <li>One ontology has been defined in the base model. The import will be added
 * to that ontology.</li>
 * <li>Multiple ontologies have been defined in the base model. The user is
 * prompted to selection the desired source ontology from the list of existing
 * ones. The import will be added to that ontology.</li>
 * </ol>
 * 
 * @author Simone Rondelli
 */
public class OntologySelector {
    protected final static String DEFAULT_NO_ONTOLOGIES_TITLE = "No ontologies exist in the model";
    protected final static String DEFAULT_NO_ONTOLOGIES_MESSAGE = "The model contains no ontologies. As such, no ontology can be selected to perform the desired action with.";

    protected final static String DEFAULT_MULTIPLE_ONTOLOGIES_TITLE = "Multiple ontologies exist in the model";
    protected final static String DEFAULT_MULTIPLE_ONTOLOGIES_MESSAGE = "Please select the ontology you would like to perform the desired action with.";

    protected OntModel model;
    protected Resource ontology;
    protected String ontologyPrefix;

    protected String noOntologiesTitle;
    protected String noOntologiesMessage;
    protected String multipleOntologiesTitle;
    protected String multipleOntologiesMessage;

    public OntologySelector(OntModel model) {
        this.model = model;
    }

    public void selectOntology() {
        Shell shell = EclipseUIPlugin.getStandardDisplay().getActiveShell();
        selectOntology(shell);
    }

    // TODO give the possibility to customize the dialogs
    // TODO: Using the shell directly from this method ointriduces problems if
    // this method is called within a non UI thread!
    public void selectOntology(Shell shell) {
        List<Resource> ontologies = OntModelUtils.getOntologies(model.getBaseModel());
        if (ontologies.isEmpty() && shell != null) {
            // no ontology exists
            String title = (noOntologiesTitle != null) ? noOntologiesTitle
                    : DEFAULT_NO_ONTOLOGIES_TITLE;
            String message = (noOntologiesMessage != null) ? noOntologiesMessage
                    : DEFAULT_NO_ONTOLOGIES_MESSAGE;
            MessageDialog.openInformation(shell, title, message);
        }
        else if (ontologies.size() == 1) {
            // use this ontology
            ontology = ontologies.get(0);
        }
        else if (shell != null) {
            // allow the user to select one of the existing ontologies
            String title = (multipleOntologiesTitle != null) ? multipleOntologiesTitle
                    : DEFAULT_MULTIPLE_ONTOLOGIES_TITLE;
            String message = (multipleOntologiesMessage != null) ? multipleOntologiesMessage
                    : DEFAULT_MULTIPLE_ONTOLOGIES_MESSAGE;
            // String title = "Ontology that should import";
            // String message =
            // "Please select the ontology you would like to perform the import.";
            ResourceSelectionDialog dialog = new ResourceSelectionDialog(shell, title, message);
            dialog.setModel(model);
            dialog.setAllowedResourceTypes(new Resource[0]);
            dialog.setResources(ontologies);
            dialog.setHierarchicalViewDisabled(true);
            dialog.setMultiSelectAllowed(false);
            if (dialog.open() == Window.OK) {
                ontology = dialog.getFirstSelectedResource();
            }
        }
    }

    /**
     * Sets the title and messsage for the selection dialog that pops up when no
     * ontologies exist in the provided model. Setting title to null will make
     * the dialog default to the standard title. Setting message to null have
     * the corresponding effect on the message of the dialog.
     */
    public void setNoOntologiesText(String title, String message) {
        noOntologiesTitle = title;
        noOntologiesMessage = message;
    }

    /**
     * Sets the title and messsage for the selection dialog that pops up when
     * multiple ontologies exist in the provided model. Setting title to null
     * will make the dialog default to the standard title. Setting message to
     * null have the corresponding effect on the message of the dialog.
     */
    public void setMultipleOntologiesText(String title, String message) {
        multipleOntologiesTitle = title;
        multipleOntologiesMessage = message;
    }

    public Resource getOntology() {
        return ontology;
    }

    public String getOntologyPrefix() {
        return ontologyPrefix;
    }
}
