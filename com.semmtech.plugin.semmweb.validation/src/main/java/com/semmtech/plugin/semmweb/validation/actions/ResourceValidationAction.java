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

package com.semmtech.plugin.semmweb.validation.actions;


import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.validation.ValidationPlugin;
import com.semmtech.plugin.semmweb.validation.ValidationPluginImages;
import com.semmtech.plugin.semmweb.validation.handlers.RunClassInstancesValidationHandler;
import com.semmtech.plugin.semmweb.validation.handlers.RunClassSubclassesValidationHandler;
import com.semmtech.plugin.semmweb.validation.handlers.RunResourceValidationHandler;


/**
 * 
 * @author Sander Stolk
 */
public class ResourceValidationAction extends Action implements IMenuCreator {
    private static Logger logger = Logger.getLogger(ResourceValidationAction.class);

    public final static String TITLE = "Validate";

    private Menu menu;

    public ResourceValidationAction() {
        super(TITLE, AS_DROP_DOWN_MENU);
        setMenuCreator(this);
    }

    @Override
    public void run() {
        logger.debug("Run executed");
        Commands.execute(RunResourceValidationHandler.ID);
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        OntResource resource = CorePlugin.getDefault().getActiveOpenResource();
        if (resource.hasRDFType(OWL.Class)) {
            return ValidationPlugin.getDefault().getImageDescriptor(
                    ValidationPluginImages.IMG_VALIDATE_OWL_CLASS);
        }
        else if (resource.hasRDFType(RDFS.Class)) {
            return ValidationPlugin.getDefault().getImageDescriptor(
                    ValidationPluginImages.IMG_VALIDATE_RDFS_CLASS);
        }
        return ValidationPlugin.getDefault()
                .getImageDescriptor(ValidationPluginImages.IMG_VALIDATE);
    }

    @Override
    public void dispose() {
        if (menu != null) {
            menu.dispose();
            menu = null;
        }
    }

    @Override
    public Menu getMenu(Control parent) {
        if (menu != null) {
            menu.dispose();
        }

        menu = new Menu(parent);
        final OntResource resource = CorePlugin.getDefault().getActiveOpenResource();

        // add action: Validate resource
        Action validateResourceAction = new Action((menu.getItemCount() + 1) + " Validate resource") {
            public void run() {
                Commands.execute(RunResourceValidationHandler.ID);
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return ResourceValidationAction.this.getImageDescriptor();
            }
        };
        ActionContributionItem item = new ActionContributionItem(validateResourceAction);
        item.fill(menu, -1);

        // Add class actions
        if (resource != null && resource.hasRDFType(RDFS.Class)) {
            // add action: Validate subclasses
            Action validateSubclassesAction = new Action((menu.getItemCount() + 1)
                    + " Validate resource + subclasses") {
                public void run() {
                    Commands.execute(RunClassSubclassesValidationHandler.ID);
                }

                @Override
                public ImageDescriptor getImageDescriptor() {
                    if (resource.hasRDFType(OWL.Class)) {
                        return ValidationPlugin.getDefault().getImageDescriptor(
                                ValidationPluginImages.IMG_VALIDATE_OWL_CLASS_HIERARCHY);
                    }
                    return ValidationPlugin.getDefault().getImageDescriptor(
                            ValidationPluginImages.IMG_VALIDATE_RDFS_CLASS_HIERARCHY);
                }
            };
            item = new ActionContributionItem(validateSubclassesAction);
            item.fill(menu, -1);

            // add action: Validate instances
            Action validateInstancesAction = new Action((menu.getItemCount() + 1)
                    + " Validate instances") {
                public void run() {
                    Commands.execute(RunClassInstancesValidationHandler.ID);
                }

                @Override
                public ImageDescriptor getImageDescriptor() {
                    if (resource.hasRDFType(OWL.Class)) {
                        return ValidationPlugin.getDefault().getImageDescriptor(
                                ValidationPluginImages.IMG_VALIDATE_INDIVIDUALS);
                    }
                    return ValidationPlugin.getDefault().getImageDescriptor(
                            ValidationPluginImages.IMG_VALIDATE_INSTANCES);
                }
            };
            item = new ActionContributionItem(validateInstancesAction);
            item.fill(menu, -1);
        }

        return menu;
    }

    @Override
    public Menu getMenu(Menu parent) {
        return null;
    }
}
