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


import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.extensionpoint.CoreExtensions;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider.InspectOrder;
import com.semmtech.plugin.semmweb.core.viewers.ResourceToolTip;
import com.semmtech.plugin.semmweb.core.widgets.OntologyEditorFormHeading;
import com.semmtech.ui.plugin.widgets.ExtendedCTabFolder;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class ModelResourceFormPage extends AbstractOntologyEditorFormPage implements
        IPropertyChangeListener {
    private OntResource resource;
    private IProject project;
    private ExtendedCTabFolder tabFolder;
    private Composite contentComposite;
    private final List<AbstractModelResourceContent> contentList;
    private final List<IModelResourceContentFactory> factories;
    private ILabelProviderListener headingLabelListener;
    private ISelectionProvider selectionProvider;
    private OntologyEditorFormHeading heading;

    public ModelResourceFormPage(FormEditor editor, String id, String title, OntResource resource,
            IProject project) {
        super(editor, id, title);

        this.editor = editor;
        this.resource = resource;
        this.project = project;
        this.contentList = Lists.newArrayList();
        this.selectionProvider = new SelectionProvider(resource);
        this.factories = Lists.newArrayList();

        findFactories();

        setContent();
    }

    /**
     * Locates the IModelResourceContentFactories defined in other plug-ins.
     */
    private void findFactories() {
        try {
            for (IConfigurationElement element : CoreExtensions
                    .getConfigurationElementsFor(CoreExtensions.MODEL_RESOURCE_CONTENT_ID)) {
                Object object = element.createExecutableExtension(CoreExtensions.CLASS_PROPERTY);
                if (object instanceof IModelResourceContentFactory) {
                    factories.add((IModelResourceContentFactory) object);
                }
            }
        }
        catch (CoreException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        for (AbstractModelResourceContent content : contentList) {
            content.dispose();
        }
        contentList.clear();

        getSite().setSelectionProvider(null);
        selectionProvider = null;

        Widgets.disposeIfExists(contentComposite);
        contentComposite = null;
        tabFolder = null;
        heading = null;
    }

    @Override
    protected void disposeListeners() {
        if (getLabelProvider() != null && headingLabelListener != null) {
            getLabelProvider().removeListener(headingLabelListener);
            headingLabelListener = null;
        }
        super.disposeListeners();
    }

    private void setContent() {
        contentList.clear();

        // The list below is the fixed set of AbstractModelResourceContent
        // controls
        contentList.add(new ModelResourcePropertiesContent(this));
        contentList.add(new ModelResourceInverseQCRsContent(this));
        contentList.add(new ModelResourcePossessedAspectsContent(this));
        contentList.add(new ModelResourceRestrictionContent(this));
        contentList.add(new ModelResourceInstancesContent(this));
        contentList.add(new ModelResourceDiagramContent(this));

        // Additional content will be added here using extension points
        for (IModelResourceContentFactory factory : factories) {
            AbstractModelResourceContent content = factory.createContent(this);
            contentList.add(content);
        }
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        super.init(site, input);
        site.setSelectionProvider(selectionProvider);
    }

    public OntResource getResource() {
        return resource;
    }

    public Composite getContentComposite() {
        return contentComposite;
    }

    public IProject getProject() {
        return project;
    }

    @SuppressWarnings("unused")
    private void refreshForm() {
        disposeListeners();
        clearFormContent();
        setContent();
        createFormContent(getManagedForm());
    }

    protected void createScrolledFormContent(ScrolledForm scrolledForm) {
        createResourceContentComposite(scrolledForm);
    }

    private void createResourceContentComposite(final ScrolledForm scrolledForm) {
        final Display display = getSite().getShell().getDisplay();
        final Composite body = scrolledForm.getBody();
        body.setLayout(createZeroGridLayout());

        heading = new OntologyEditorFormHeading(body, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(heading);
        heading.setText(getLabelProvider().getText(resource));
        heading.setImage(getLabelProvider().getImage(resource,
                InspectOrder.CLASS_PROPERTY_INDIVIDUAL));
        heading.setToolBarAlignment(SWT.BOTTOM);
        heading.setColors(toolkit.getColors());
        heading.decorate();
        heading.setSeparatorVisible(false);

        headingLabelListener = new ILabelProviderListener() {
            @Override
            public void labelProviderChanged(LabelProviderChangedEvent event) {
                Object[] changedElements = event.getElements();
                if (changedElements == null || Arrays.asList(changedElements).contains(resource)) {
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            heading.setImage(getLabelProvider().getImage(resource,
                                    InspectOrder.CLASS_PROPERTY_INDIVIDUAL));
                        }
                    });
                }
            }
        };
        getLabelProvider().addListener(headingLabelListener);

        DndUtils.addDragSupport(heading.getImageLabel(), resource);
        ResourceToolTip tooltip = new ResourceToolTip(heading.getImageLabel());
        tooltip.setModelProvider(this);
        tooltip.setResource(resource);

        tabFolder = new ExtendedCTabFolder(heading.getHeadClientComposite(), SWT.TOP);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.END).grab(false, true).indent(15, 0)
                .applyTo(tabFolder);
        tabFolder.setSimple(false);
        tabFolder.setBorderVisible(true);
        tabFolder.setUnselectedCloseVisible(false);
        tabFolder.setUnselectedTextVisible(false);

        // Gradients work better than their non-gradient counterparts.
        tabFolder.setBackground(new Color[] { new Color(display, 220, 220, 220),
                new Color(display, 220, 220, 220) }, new int[] { 100 }, true);
        tabFolder.setSelectionBackground(new Color[] { display.getSystemColor(SWT.COLOR_WHITE),
                display.getSystemColor(SWT.COLOR_WHITE) }, new int[] { 100 }, true);

        contentComposite = toolkit.createComposite(body, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(contentComposite);
        tabFolder.setContentToShowIn(contentComposite);

        // Add content appropriate for the current resource
        for (AbstractModelResourceContent content : contentList) {
            content.setToolBarManager(heading.getToolBarManager());
            tabFolder.addItem(content, content.getTitle(), content.getImage());
        }

        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Widgets.layoutControlUpToScrollableParent(contentComposite);
            }
        });

        tabFolder.setSelection(0);

        updateAvailabilityContent();
    }

    private void updateAvailabilityContent() {
        for (AbstractModelResourceContent content : contentList) {
            boolean isViewable = false;
            try {
                isViewable = content.isViewable();
            }
            catch (Throwable t) {
                // ensure the ModelResourceFormPage doesn't break
                logger.error(t.getMessage(), t);
            }

            if (isViewable) {
                tabFolder.showItem(content);
            }
            else {
                tabFolder.hideItem(content);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        updateAvailabilityContent();
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        updateAvailabilityContent();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        updateAvailabilityContent();
    }

    private static class SelectionProvider implements ISelectionProvider {

        private Resource resource;

        public SelectionProvider(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void addSelectionChangedListener(ISelectionChangedListener listener) {
        }

        @Override
        public ISelection getSelection() {
            return new StructuredSelection(resource);
        }

        @Override
        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        }

        @Override
        public void setSelection(ISelection selection) {
        }

    }
}