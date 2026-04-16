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

package com.semmtech.plugin.semmweb.core.forms.editor;


import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Optional;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreference;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreferences;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.preferences.ResourceEditorPreference;
import com.semmtech.plugin.semmweb.core.widgets.PossessedAspectsContentPart;
import com.semmtech.semantics.ontology.OntClassUtil;
import com.semmtech.semantics.ontology.OntResourceUtil;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class ModelResourcePossessedAspectsContent extends AbstractModelResourceContent {
    private static final Logger logger = Logger.getLogger(AbstractModelResourceContent.class);

    private PossessedAspectsContentPart possessedAspectsPart;

    public ModelResourcePossessedAspectsContent(ModelResourceFormPage page) {
        super(page);
    }

    @Override
    public String getTitle() {
        return "Possessed Aspects";
    }

    @Override
    public Image getImage() {
        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_POSSESSED_ASPECTS);
    }

    @Override
    public boolean isViewable() {
        ResourceEditorClassPreferences preferences = ResourceEditorPreference.fromProject(
                page.getProject()).getResourceEditorPreferences();

        Optional<Boolean> show = Optional.absent();
        for (ExtendedIterator<Resource> iter = getResource().listRDFTypes(false); iter.hasNext();) {
            Resource type = iter.next();
            int setting = preferences.getPossessedAspectsSetting(type);
            if ((setting & ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES) != 0) {
                show = Optional.fromNullable(Boolean.TRUE);
            }
            if (show.isPresent()) {
                break;
            }
        }

        if (!show.isPresent() && OntResourceUtil.isClass(getResource())) {
            OntClass clazz = JenaUtil.asOntClass(getResource());
            for (ExtendedIterator<OntClass> iter = OntClassUtil.listSuperClasses(clazz); iter
                    .hasNext();) {
                OntClass superClass = iter.next();
                int setting = preferences.getPossessedAspectsSetting(superClass);
                if ((setting & ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES) != 0) {
                    show = Optional.fromNullable(Boolean.TRUE);
                }
                if (show.isPresent()) {
                    break;
                }
            }
        }
        return show.or(Boolean.FALSE).booleanValue();
    }

    @Override
    protected Control createContent(Composite parent) {
        FormToolkit toolkit = getToolkit();
        Composite outerComposite = toolkit.createComposite(parent, SWT.NONE);
        outerComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                TableWrapData.FILL_GRAB));

        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(5, 0).applyTo(outerComposite);
        createPossessedAspects(outerComposite);

        return outerComposite;
    }

    @Override
    public void fillToolBar(IToolBarManager toolBarManager) {
        // Show owner class
        IAction toggleAction = new Action("Show owner class", IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                boolean checked = isChecked();
                if (possessedAspectsPart != null) {
                    possessedAspectsPart.setShowOwnerClass(checked);
                }
            }
        };
        toggleAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                CorePlugin.PLUGIN_ID, CorePluginImages.IMG_SUPER_CLASS));
        toggleAction.setChecked(possessedAspectsPart.getShowOwnerClass());
        toolBarManager.add(toggleAction);
        toolBarManager.update(true);

        // Add Aspect
        IAction addPropertyAction = new Action("Add Aspect...") {
            @Override
            public void run() {
                if (possessedAspectsPart != null) {
                    possessedAspectsPart.executeAddAspect();
                }
            }
        };
        addPropertyAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                CorePlugin.PLUGIN_ID, CorePluginImages.IMG_ADD_PLUS));
        toolBarManager.add(addPropertyAction);
        toolBarManager.update(true);
    }

    private void createPossessedAspects(Composite parent) {
        possessedAspectsPart = new PossessedAspectsContentPart(this, parent, getToolkit());
        GridDataFactory.fillDefaults().grab(true, true).indent(0, 0).applyTo(possessedAspectsPart);
    }

    @Override
    public void notifyEvent(IModelEvent event) {
        updateContent();
    }

    @Override
    public void updateContent() {
        if (!Widgets.isNullOrDisposed(possessedAspectsPart)) {
            possessedAspectsPart.refresh();
        }
        refresh();
    }

    /**
     * Refreshes the form (layout every descendant).
     */
    public void refresh() {
        logger.debug("(" + getResource().toString() + ") refreshForm called!");
        super.refresh();
    }

}
