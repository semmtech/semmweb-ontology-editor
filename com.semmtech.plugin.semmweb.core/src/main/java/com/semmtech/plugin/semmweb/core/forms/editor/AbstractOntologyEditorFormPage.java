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


import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.OpenResourceEventListener;
import com.semmtech.plugin.semmweb.core.ui.IOpenResourcesProvider;
import com.semmtech.plugin.semmweb.core.widgets.ResourceSidebar;
import com.semmtech.plugin.semmweb.core.widgets.ResourceSidebar.ResourceSidebarSettings;
import com.semmtech.ui.plugin.DelayedRunnableExecution;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public abstract class AbstractOntologyEditorFormPage extends AbstractModelFormPage implements
        OpenResourceEventListener {

    private static int SIDEBAR_WIDTH = 30;
    private static int EDITOR_TAB_HEIGHT = 25;
    private static int MARGIN_SIZE = 2;

    protected FormEditor editor;
    private Composite editorAreaComposite;
    private ControlListener editorAreaResizeListener;
    private final DelayedRunnableExecution onEditorAreaResizeExecution;
    protected FormToolkit toolkit;
    protected Composite sidebarContainer;
    private ResourceSidebar sidebar;
    protected Composite containerComposite;

    public AbstractOntologyEditorFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        this.editor = editor;
        this.toolkit = editor.getToolkit();
        if (editor instanceof IOpenResourcesProvider) {
            IOpenResourcesProvider resourcesProvider = (IOpenResourcesProvider) editor;
            resourcesProvider.addOpenResourceEventListener(this);
        }
        this.onEditorAreaResizeExecution = new DelayedRunnableExecution(new Runnable() {
            @Override
            public void run() {
                // resize container composite
                if (!Widgets.isNullOrDisposed(containerComposite)) {
                    GridData gridData = (GridData) containerComposite.getLayoutData();
                    containerComposite.setRedraw(false);
                    // Set appropriate size for container composite
                    gridData.widthHint = getAvailableWidthMainContent();
                    gridData.heightHint = getAvailableHeight();
                    Widgets.layoutControlUpToScrollableParent(containerComposite);
                    containerComposite.setRedraw(true);

                    // Refresh sidebar to deal with the new height
                    refreshSidebar();
                }
            }
        }, 50, 10);
    }

    protected void disposeListeners() {
        if (!Widgets.isNullOrDisposed(editorAreaComposite) && editorAreaResizeListener != null) {
            editorAreaComposite.removeControlListener(editorAreaResizeListener);
            editorAreaResizeListener = null;
        }
        if (editor instanceof IOpenResourcesProvider) {
            IOpenResourcesProvider resourcesProvider = (IOpenResourcesProvider) editor;
            resourcesProvider.removeOpenResourceEventListener(this);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        disposeListeners();

        Widgets.disposeIfExists(sidebarContainer);
        Widgets.disposeIfExists(containerComposite);
        // RuntimeException: Widget disposed too early for part
        // com.semmtech.plugin.semmweb.core.editors.OntologyEditor
        // Widgets.disposeIfExists(editorAreaComposite);

        sidebar = null;
        sidebarContainer = null;
        containerComposite = null;
    }

    public FormEditor getEditor() {
        return editor;
    }

    public FormToolkit getToolkit() {
        return toolkit;
    }

    public void openStartPage() {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            return;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        editor.openStartPage();
    }

    public void openResource(Resource resource) {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            return;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        editor.openResource(resource);
    }

    public void closeResource(Resource resource) {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            return;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        editor.closeResource(resource);
    }

    public void closeAllResourcesBut(Resource resource) {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            return;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        editor.closeAllResourcesBut(resource);
    }

    public void closeAllResources() {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            return;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        editor.closeAllResources();
    }

    public List<OntResource> getOpenResources() {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            List<OntResource> emptyList = Lists.newArrayList();
            return emptyList;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        return editor.getOpenResources();
    }

    public ResourceSidebarSettings getOpenResourceSidebarSettings() {
        if (!(getEditor() instanceof OntologyFormEditor)) {
            return null;
        }

        OntologyFormEditor editor = (OntologyFormEditor) getEditor();
        return editor.getOpenResourceSidebarSettings();
    }

    protected void clearFormContent() {
        ScrolledForm scrolledForm = getManagedForm().getForm();
        for (Control child : scrolledForm.getBody().getChildren()) {
            child.dispose();
        }
        scrolledForm.getBody().layout(true, true);
    }

    @Override
    public void setActive(boolean active) {
        refreshSidebar();
        super.setActive(active);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        ScrolledForm scrolledForm = managedForm.getForm();
        scrolledForm.getVerticalBar().dispose(); // dispose entirely
        scrolledForm.getHorizontalBar().dispose(); // dispose entirely
        Composite body = scrolledForm.getBody();
        body.setLayout(createZeroGridLayout(2, false));

        setListenerOnEditorAreaResize();

        createSidebar(body);
        createMainContentComposite(body);

        scrolledForm.getBody().layout(true, true);
        setPageInitialized(true);
    }

    private void setListenerOnEditorAreaResize() {
        Composite formBody = getManagedForm().getForm().getBody();
        editorAreaComposite = Widgets.retrieveFirstParentOfType(formBody, CTabFolder.class);
        for (int i = 0; (i < 3) && (editorAreaComposite != null); i++) {
            editorAreaComposite = editorAreaComposite.getParent();
        }

        if (editorAreaResizeListener != null) {
            editorAreaComposite.removeControlListener(editorAreaResizeListener);
        }
        editorAreaResizeListener = new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                onEditorAreaResizeExecution.start();
                onEditorAreaResizeExecution.poke();
            }
        };
        editorAreaComposite.addControlListener(editorAreaResizeListener);
    }

    private int getAvailableWidthMainContent() {
        if (Widgets.isNullOrDisposed(editorAreaComposite)) {
            return 0;
        }
        int consumedWidth = SIDEBAR_WIDTH + 2 * MARGIN_SIZE;
        return Math.max(0, editorAreaComposite.getClientArea().width - consumedWidth);
    }

    private int getAvailableHeight() {
        if (Widgets.isNullOrDisposed(editorAreaComposite)) {
            return 0;
        }
        int consumedHeight = EDITOR_TAB_HEIGHT + 2 * MARGIN_SIZE;
        return Math.max(0, editorAreaComposite.getClientArea().height - consumedHeight);
    }

    private void createMainContentComposite(final Composite parent) {
        containerComposite = toolkit.createComposite(parent);

        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
                .hint(getAvailableWidthMainContent(), getAvailableHeight())
                .applyTo(containerComposite);
        containerComposite.setLayout(createZeroGridLayout());

        ScrolledForm scrolledForm = toolkit.createScrolledForm(containerComposite);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(scrolledForm);

        createScrolledFormContent(scrolledForm);
    }

    protected abstract void createScrolledFormContent(ScrolledForm scrolledForm);

    protected void createSidebar(final Composite parent) {
        Display display = parent.getDisplay();
        Color backgroundColor = display.getSystemColor(SWT.COLOR_WHITE);

        sidebarContainer = toolkit.createComposite(parent, SWT.NONE);
        sidebarContainer.setBackground(backgroundColor);
        sidebarContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
        sidebarContainer.setLayout(createZeroGridLayout());

        int availableHeight = getAvailableHeight();
        sidebar = new ResourceSidebar(sidebarContainer, this, availableHeight);
        GridDataFactory.fillDefaults().hint(SWT.DEFAULT, availableHeight)
                .align(SWT.BEGINNING, SWT.BEGINNING).applyTo(sidebar);
    }

    protected Menu createContextMenu(Control control, IMenuListener menuListener) {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu(control);
        menuManager.setRemoveAllWhenShown(true);
        menuManager.addMenuListener(menuListener);
        control.setMenu(menu);
        return menu;
    }

    @Override
    public void resourceActivated(OntResource resource) {
        refreshSidebar(resource);
    }

    @Override
    public void resourceOpened(OntResource resource) {
        refreshSidebar(resource);
    }

    @Override
    public void resourceClosed(OntResource resource) {
    }

    @Override
    public void resourcesClosed(List<OntResource> resources) {
        refreshSidebar();
    }

    protected void refreshSidebar(OntResource resource) {
        if (Widgets.isNullOrDisposed(sidebarContainer) || Widgets.isNullOrDisposed(sidebar)) {
            return;
        }

        if (isActive()) {
            sidebar.refresh();
            if (resource != null) {
                sidebar.setResourceInDisplayedArea(resource);
            }
        }
    }

    protected void refreshSidebar() {
        if (Widgets.isNullOrDisposed(sidebarContainer) || Widgets.isNullOrDisposed(sidebar)) {
            return;
        }

        if (isActive()) {
            int availableHeight = getAvailableHeight();
            sidebarContainer.setRedraw(false);
            GridDataFactory.fillDefaults().hint(SWT.DEFAULT, availableHeight)
                    .align(SWT.BEGINNING, SWT.BEGINNING).applyTo(sidebar);
            sidebar.setAvailableHeight(availableHeight); // effects a refresh
            sidebarContainer.setRedraw(true);
        }
    }

    protected GridLayout createZeroGridLayout() {
        return createZeroGridLayout(1, false);
    }

    protected GridLayout createZeroGridLayout(int numColumns, boolean makeColumnsEqualWidth) {
        GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        return layout;
    }
}
