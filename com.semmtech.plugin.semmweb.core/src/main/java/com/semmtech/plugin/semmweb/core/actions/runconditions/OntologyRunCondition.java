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

package com.semmtech.plugin.semmweb.core.actions.runconditions;


import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.actions.IRunCondition;
import com.semmtech.plugin.semmweb.core.actions.ModelFileAction;
import com.semmtech.plugin.semmweb.core.handlers.RenameModelHandler;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;


/**
 * A run condition that is satisfied when the action is a ModelFileAction and
 * when that action contains the specified number of ontologies in the base
 * model it obtains (i.e. none, a single ontology, or multiple ontologies).
 * 
 * @author Sander Stolk
 */
public class OntologyRunCondition implements IRunCondition {
    public static int ALLOW_NONE = 1;
    public static int ALLOW_SINGLE = 2;
    public static int ALLOW_MULTIPLE = 4;

    protected final static String DEFAULT_NO_ONTOLOGIES_TITLE = "This model does not contain an ontology";
    protected final static String DEFAULT_NO_ONTOLOGIES_MESSAGE = "To perform the selected action, the model requires an ontology resource. At present, however, such an ontology resource does not exist in the model.";

    protected final static String DEFAULT_SINGLE_ONTOLOGY_TITLE = "This model contains an ontology";
    protected final static String DEFAULT_SINGLE_ONTOLOGY_MESSAGE = "To perform the selected action, the model should not contain a single ontology resource. At present, however, such an ontology resource exists in the model.";

    protected final static String DEFAULT_MULTIPLE_ONTOLOGIES_TITLE = "This model contains multiple ontologies";
    protected final static String DEFAULT_MULTIPLE_ONTOLOGIES_MESSAGE = "To perform the selected action, the model must not contain multiple ontology resources. At present, however, it does.";

    protected final int allowMask;
    protected final boolean promptCreation;

    protected String noOntologiesTitle;
    protected String noOntologiesMessage;
    protected String singleOntologyTitle;
    protected String singleOntologyMessage;
    protected String multipleOntologiesTitle;
    protected String multipleOntologiesMessage;

    /**
     * Sets <code>allowMask</code> to ALLOW_SINGLE and sets
     * <code>promptCreation</code> to true.
     */
    public OntologyRunCondition() {
        this(ALLOW_SINGLE, true);
    }

    /**
     * The argument <code>promptCreation</code> is used to set whether the user
     * should be prompted to create a new ontology in the case none yet exists
     * in the base model obtained by the ModelFileAction.
     */
    public OntologyRunCondition(int allowMask, boolean promptCreation) {
        this.allowMask = allowMask;
        this.promptCreation = promptCreation;
    }

    @Override
    public boolean isSatisfied(IAction action) {
        if (action instanceof ModelFileAction) {
            ModelFileAction modelFileAction = (ModelFileAction) action;
            OntModel model = modelFileAction.getModel();
            if (model == null) {
                return false;
            }

            Shell shell = Display.getDefault().getActiveShell();
            List<Resource> ontologies = OntModelUtils.getOntologies(model.getBaseModel());
            // A single ontology
            if (ontologies.size() == 1) {
                if ((allowMask & ALLOW_SINGLE) != 0) {
                    return true;
                }
                String title = (singleOntologyTitle != null) ? singleOntologyTitle
                        : DEFAULT_SINGLE_ONTOLOGY_TITLE;
                String message = (singleOntologyMessage != null) ? singleOntologyMessage
                        : DEFAULT_SINGLE_ONTOLOGY_MESSAGE;
                MessageDialog.openInformation(shell, title, message);
                return false;
            }

            // No ontologies
            if (ontologies.isEmpty()) {
                if ((allowMask & ALLOW_NONE) != 0) {
                    return true;
                }
                String title = (noOntologiesTitle != null) ? noOntologiesTitle
                        : DEFAULT_NO_ONTOLOGIES_TITLE;
                String message = (noOntologiesMessage != null) ? noOntologiesMessage
                        : DEFAULT_NO_ONTOLOGIES_MESSAGE;
                if (!promptCreation) {
                    MessageDialog.openInformation(shell, title, message);
                }
                else {
                    message += "\n\nPlease proceed in order to define a new ontology.\n"
                            + "After having done so, feel free to retry performing the current action.";
                    if (MessageDialog.openConfirm(shell, title, message)) {
                        IFile file = modelFileAction.getFile();
                        RenameModelHandler.createAction(file).run();
                    }
                }
            }
            // Multiple ontologies
            else {
                if ((allowMask & ALLOW_MULTIPLE) != 0) {
                    return true;
                }
                String title = (multipleOntologiesTitle != null) ? multipleOntologiesTitle
                        : DEFAULT_MULTIPLE_ONTOLOGIES_TITLE;
                String message = (multipleOntologiesMessage != null) ? multipleOntologiesMessage
                        : DEFAULT_MULTIPLE_ONTOLOGIES_MESSAGE;
                MessageDialog.openInformation(shell, title, message);
            }

        }
        return false;
    }

    /**
     * Sets the title and messsage for the error dialog that pops up when no
     * ontologies exist in the provided model. Setting title to null will make
     * the dialog default to the standard title. Setting message to null have
     * the corresponding effect on the message of the dialog.
     */
    public void setNoOntologiesText(String title, String message) {
        noOntologiesTitle = title;
        noOntologiesMessage = message;
    }

    /**
     * Sets the title and messsage for the error dialog that pops up when a
     * single ontology exists in the provided model. Setting title to null will
     * make the dialog default to the standard title. Setting message to null
     * have the corresponding effect on the message of the dialog.
     */
    public void setSingleOntologyText(String title, String message) {
        singleOntologyTitle = title;
        singleOntologyMessage = message;
    }

    /**
     * Sets the title and messsage for the error dialog that pops up when
     * multiple ontologies exist in the provided model. Setting title to null
     * will make the dialog default to the standard title. Setting message to
     * null have the corresponding effect on the message of the dialog.
     */
    public void setMultipleOntologiesText(String title, String message) {
        multipleOntologiesTitle = title;
        multipleOntologiesMessage = message;
    }

}
