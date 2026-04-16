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


import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreference;
import com.semmtech.plugin.semmweb.core.model.ResourceEditorClassPreferences;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.preferences.ResourceEditorPreference;
import com.semmtech.plugin.semmweb.core.widgets.InverseQCRContentPart;
import com.semmtech.semantics.ontology.OntClassUtil;
import com.semmtech.semantics.ontology.OntResourceUtil;


/**
 * 
 * @author Sander Stolk
 */
public class ModelResourceInverseQCRsContent extends AbstractModelResourceContent {
    private static final Logger logger = Logger.getLogger(ModelResourceInverseQCRsContent.class);

    private final Set<InverseQCRContentPart> inverseQCRParts;
    private final ResourceEditorClassPreferences preferences;

    public ModelResourceInverseQCRsContent(ModelResourceFormPage page) {
        super(page);
        inverseQCRParts = Sets.newHashSet();
        preferences = ResourceEditorPreference.fromProject(page.getProject())
                .getResourceEditorPreferences();
    }

    @Override
    public String getTitle() {
        return "Qualified Cardinalities";
    }

    @Override
    public Image getImage() {
        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_QUALIFIED_CARDINALITY);
    }

    @Override
    public boolean isViewable() {
        return hasQCRs();
    }

    @Override
    protected Control createContent(Composite parent) {
        FormToolkit toolkit = getToolkit();
        Composite outerComposite = toolkit.createComposite(parent, SWT.NONE);
        outerComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                TableWrapData.FILL_GRAB));

        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(5, 15).applyTo(outerComposite);
        createInverseQCRs(outerComposite);

        return outerComposite;
    }

    public boolean hasQCRs() {
        for (String propertyUri : getQCRPropertyURIs()) {
            OntProperty leftProperty = getModelProvider().getOntModel().getOntProperty(propertyUri);
            if (leftProperty == null) {
                continue;
            }
            OntProperty rightProperty = leftProperty.getInverseOf();
            if (rightProperty == null) {
                rightProperty = leftProperty.getInverse();
            }

            if (rightProperty != null) {
                return true;
            }
        }
        return false;
    }

    private void createInverseQCRs(Composite parent) {
        if (inverseQCRParts.size() > 0) {
            for (InverseQCRContentPart part : inverseQCRParts) {
                part.dispose();
            }
            inverseQCRParts.clear();
        }
        for (String propertyUri : getQCRPropertyURIs()) {
            OntProperty leftProperty = getModelProvider().getOntModel().getOntProperty(propertyUri);
            if (leftProperty == null) {
                continue;
            }
            OntProperty rightProperty = leftProperty.getInverseOf();
            if (rightProperty == null) {
                rightProperty = leftProperty.getInverse();
            }

            if (rightProperty != null) {
                InverseQCRContentPart part = new InverseQCRContentPart(this, parent, getToolkit(),
                        leftProperty, rightProperty);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(part);
                // part.setLayoutData(new GridData(GridData.FILL,
                // GridData.BEGINNING, true, false, 1, 1));
                inverseQCRParts.add(part);
            }
        }
    }

    private Set<String> getQCRPropertyURIs() {
        Set<String> propertyUris = Sets.newHashSet();
        for (ExtendedIterator<Resource> iter = getResource().listRDFTypes(false); iter.hasNext();) {
            Resource type = iter.next();
            if (!preferences.containsClassURI(type.getURI())) {
                continue;
            }
            ResourceEditorClassPreference pref = preferences.getPreference(type);
            for (String propertyUri : pref.getQCRPropertyURIs()) {
                if ((pref.getQCRPropertySetting(propertyUri) & ResourceEditorClassPreference.SETTING_SHOW_ON_INSTANCES) != 0) {
                    propertyUris.add(propertyUri);
                }
            }
        }
        if (OntResourceUtil.isClass(getResource())) {
            for (ExtendedIterator<OntClass> iter = OntClassUtil.listSuperClasses(getResource().as(
                    OntClass.class)); iter.hasNext();) {
                OntClass superClass = iter.next();
                if (!preferences.containsClassURI(superClass.getURI())) {
                    continue;
                }
                ResourceEditorClassPreference pref = preferences.getPreference(superClass);
                for (String propertyUri : pref.getQCRPropertyURIs()) {
                    if ((pref.getQCRPropertySetting(propertyUri) & ResourceEditorClassPreference.SETTING_SHOW_ON_SUBCLASSES) != 0) {
                        propertyUris.add(propertyUri);
                    }
                }
            }
        }
        return propertyUris;
    }

    @Override
    public void notifyEvent(IModelEvent event) {
        updateContent();
    }

    @Override
    public void updateContent() {
        for (InverseQCRContentPart part : inverseQCRParts) {
            part.updateWithModelInformation();
        }
        refresh();
    }

    /**
     * Refreshes the form (layout every descendant, and reflows).
     */
    public void refresh() {
        logger.debug("(" + getResource().toString() + ") refreshForm called!");
        super.refresh();
    }

}
