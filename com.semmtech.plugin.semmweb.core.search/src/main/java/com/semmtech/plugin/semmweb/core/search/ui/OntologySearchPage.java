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

package com.semmtech.plugin.semmweb.core.search.ui;


import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.internal.ui.text.LineElement;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;

import com.google.common.collect.Sets;
import com.semmtech.plugin.semmweb.core.search.OntologySearchQuery;
import com.semmtech.plugin.semmweb.core.search.OntologySearchTarget;


@SuppressWarnings("restriction")
public class OntologySearchPage extends DialogPage implements ISearchPage {

    private static Logger logger = Logger.getLogger(OntologySearchPage.class);

    private ISearchPageContainer container;
    private Text searchText;
    private Button caseSensitiveCheckbox;
    private Button uriRadio;
    private Button literalRadio;

    public OntologySearchPage() {
        super("Ontology Search");
    }

    public OntologySearchPage(String title) {
        super(title);
    }

    public OntologySearchPage(String title, ImageDescriptor image) {
        super(title, image);
    }

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite top = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        top.setLayout(layout);

        Label label = new Label(top, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1));
        label.setText("Search string:");

        searchText = new Text(top, SWT.BORDER);
        searchText.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        caseSensitiveCheckbox = new Button(top, SWT.CHECK);
        caseSensitiveCheckbox.setText("Case sensitive");

        Group searchForGroup = new Group(top, SWT.SHADOW_ETCHED_IN);
        searchForGroup.setText("Search For");
        searchForGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
        layout = new GridLayout(1, false);
        layout.verticalSpacing = 7;
        searchForGroup.setLayout(layout);

        literalRadio = new Button(searchForGroup, SWT.RADIO);
        literalRadio.setText("Literal");
        literalRadio.setSelection(true);

        uriRadio = new Button(searchForGroup, SWT.RADIO);
        uriRadio.setText("URI");

        setControl(top);

        searchText.setFocus();
    }

    private FileTextSearchScope getSelectedResourcesScope() {
        HashSet<Object> resources = Sets.newHashSet();
        ISelection sel = getContainer().getSelection();
        if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
            Iterator<?> iter = ((IStructuredSelection) sel).iterator();
            while (iter.hasNext()) {
                Object curr = iter.next();
                if (curr instanceof IWorkingSet) {
                    IWorkingSet workingSet = (IWorkingSet) curr;
                    if (workingSet.isAggregateWorkingSet() && workingSet.isEmpty()) {
                        return FileTextSearchScope.newWorkspaceScope(getExtensions(), false);
                    }
                    IAdaptable[] elements = workingSet.getElements();
                    for (int i = 0; i < elements.length; i++) {
                        IResource resource = (IResource) elements[i].getAdapter(IResource.class);
                        if (resource != null && resource.isAccessible()) {
                            resources.add(resource);
                        }
                    }
                }
                else if (curr instanceof LineElement) {
                    IResource resource = ((LineElement) curr).getParent();
                    if (resource != null && resource.isAccessible()) {
                        resources.add(resource);
                    }
                }
                else if (curr instanceof IAdaptable) {
                    IResource resource = (IResource) ((IAdaptable) curr)
                            .getAdapter(IResource.class);
                    if (resource != null && resource.isAccessible()) {
                        resources.add(resource);
                    }
                }
            }
        }
        else if (getContainer().getActiveEditorInput() != null) {
            resources.add(getContainer().getActiveEditorInput().getAdapter(IFile.class));
        }
        IResource[] arr = resources.toArray(new IResource[resources.size()]);
        return FileTextSearchScope.newSearchScope(arr, getExtensions(), false);
    }

    public String[] getExtensions() {
        return new String[] { "*.ttl", "*.rdf", "*.owl" };
    }

    public FileTextSearchScope createTextSearchScope() {
        // Setup search scope
        switch (getContainer().getSelectedScope()) {
        case ISearchPageContainer.WORKSPACE_SCOPE:
            return FileTextSearchScope.newWorkspaceScope(getExtensions(), false);
        case ISearchPageContainer.SELECTION_SCOPE:
            return getSelectedResourcesScope();
        case ISearchPageContainer.WORKING_SET_SCOPE:
            IWorkingSet[] workingSets = getContainer().getSelectedWorkingSets();
            return FileTextSearchScope.newSearchScope(workingSets, getExtensions(), false);
        default:
            // unknown scope
            return FileTextSearchScope.newWorkspaceScope(getExtensions(), false);
        }
    }

    @Override
    public boolean performAction() {
        try {
            FileTextSearchScope scope = createTextSearchScope();
            OntologySearchTarget target = OntologySearchTarget.LITERAL;
            if (uriRadio.getSelection()) {
                target = OntologySearchTarget.URI;
            }
            ISearchQuery query = new OntologySearchQuery(searchText.getText(), false,
                    caseSensitiveCheckbox.getSelection(), target, scope);
            NewSearchUI.runQueryInBackground(query);
        }
        catch (Exception ex) {
            logger.error("An error occurred while executing search query", ex);
        }

        return true;
    }

    @Override
    public void setContainer(ISearchPageContainer container) {
        this.container = container;
    }

    protected ISearchPageContainer getContainer() {
        return container;
    }

}
