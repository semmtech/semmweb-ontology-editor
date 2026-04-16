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

package com.semmtech.plugin.semmweb.core.widgets;


import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;
import com.semmtech.plugin.semmweb.core.ui.forms.IEditorFormColors;
import com.semmtech.plugin.semmweb.core.ui.forms.RestrictionFormColors;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class PropertyListContentPart extends AbstractPropertyObjectContentPart {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PropertyListContentPart.class);

    private Property property;
    private Composite composite;
    private Composite membersComposite;

    private FormColors formColors;
    private FormToolkit toolkit;
    private Font boldFont;

    private Resource list;
    private List<Resource> initialMembers = Lists.newLinkedList();
    private List<Resource> members = Lists.newLinkedList();
    private List<Resource> locallyCreated = Lists.newArrayList();

    private boolean dirty = false;
    @SuppressWarnings("unused")
    private Listener closeListener;

    private IOperationHistory operationHistory;
    private IUndoContext undoContext;
    private List<AbstractOperation> listFormOperationsHistory = Lists.newArrayList();
    private int executionIndexFormOperationsHistory = 0; // contains the first
                                                         // non-executed index
                                                         // in
                                                         // listFormOperationsHistory

    public PropertyListContentPart(AbstractModelResourceContent contentParent, Composite parent,
            FormToolkit toolkit, Property property, Resource list) {
        super(contentParent, parent, toolkit);
        this.list = list;
        this.property = property;

        initialize();
        createContent();
    }

    @Override
    public RDFNode getObject() {
        return list;
    }

    public void alterContent(Resource list) {
        this.list = list;
        initialize();
        fillContent();
        refresh();
    }

    // TODO
    @Override
    public void dispose() {
        if (boldFont != null && !boldFont.isDisposed()) {
            boldFont.dispose();
        }
        if (operationHistory != null) {
            operationHistory.dispose(undoContext, true, true, false);
            operationHistory = null;
            undoContext = null;
        }

        super.dispose();
    }

    private void createOperationHistory() {
        IWorkbench workbench = CorePlugin.getDefault().getWorkbench();
        operationHistory = workbench.getOperationSupport().getOperationHistory();
        undoContext = getModelProvider().getUndoContext();
    }

    private void initialize() {
        initializeMembers();
    }

    /**
     * Initializes members
     */
    private void initializeMembers() {
        initialMembers = getMembers(list);
        members = new LinkedList<>(initialMembers);
    }

    private LinkedList<Resource> getMembers(Resource list) {
        LinkedList<Resource> members = Lists.newLinkedList();
        if (list != null && !RDF.nil.equals(list)) {
            Resource first = list.getPropertyResourceValue(RDF.first);
            if (first != null) {
                members.add(first);
                Resource rest = list.getPropertyResourceValue(RDF.rest);
                while (rest != null && !RDF.nil.equals(rest)) {
                    members.add(rest.getPropertyResourceValue(RDF.first));
                    rest = rest.getPropertyResourceValue(RDF.rest);
                }
            }
        }
        return members;
    }

    private void createContent() {
        formColors = new RestrictionFormColors(Display.getCurrent());
        toolkit = new FormToolkit(formColors);
        toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        setBackground(formColors.getColor(IEditorFormColors.WHITE));
        {
            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 2;
            layout.verticalSpacing = 0;
            layout.topMargin = 1;
            layout.rightMargin = 0;
            layout.leftMargin = 2;
            layout.bottomMargin = 3;
            layout.horizontalSpacing = 3;
            setLayout(layout);
        }

        Label iconLabel = new Label(this, SWT.NONE);
        {
            TableWrapData data = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP);
            data.heightHint = 18;
            iconLabel.setLayoutData(data);
        }
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent event) {
                CorePlugin.getDefault().openResource(list);
            }
        });
        iconLabel.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_RDF_LIST));

        composite = toolkit.createComposite(this, SWT.BORDER);
        composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
        {
            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 1;
            layout.verticalSpacing = 0;
            layout.topMargin = 2;
            layout.bottomMargin = 7;
            layout.rightMargin = 0;
            layout.leftMargin = 2;
            layout.horizontalSpacing = 8;
            composite.setLayout(layout);
        }
        composite.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                clearListFormOperationHistory();
            }
        });

        DropTarget dropTarget = new DropTarget(composite, DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { ResourceTransfer.getInstance(),
                ResourceArrayListTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (ResourceArrayListTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    ResourceArrayList list = (ResourceArrayList) event.data;
                    addMembers(list);
                }
                else if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    addMember((Resource) event.data);
                }
            }
        });

        fillContent();
    }

    private void fillContent() {
        clearListFormOperationHistory();
        createOperationHistory();
        refreshProperties();
    }

    @Override
    public boolean updateContent() {
        boolean updateRequired = false;
        LinkedList<Resource> currentMembers = getMembers(list);
        LinkedList<Resource> storedMembers = new LinkedList<>(members);
        if (currentMembers.size() != storedMembers.size()) {
            updateRequired = true;
        }
        else {
            for (int i = 0; i < currentMembers.size(); i++) {
                if (!currentMembers.get(i).equals(storedMembers.get(i))) {
                    updateRequired = true;
                }
            }
        }
        if (updateRequired) {
            refreshProperties();
        }
        return updateRequired;
    }

    private static void removeFromOperationHistory(AbstractOperation operation,
            IOperationHistory history) {
        IUndoableOperation[] emptyArray = new IUndoableOperation[0];
        history.replaceOperation(operation, emptyArray);
    }

    private void removeFromOperationHistory(List<AbstractOperation> operations,
            IOperationHistory history) {
        for (AbstractOperation operation : operations) {
            removeFromOperationHistory(operation, history);
        }
    }

    private void commit() {
        // Remove the edit history of working in the component containing the
        // list
        clearListFormOperationHistory();

        if (dirty == false) {
            return;
        }

        ModelTransaction transaction = getModelProvider().createTransaction("Committing a list");
        createListStatements();
        getModelProvider().commitTransaction(transaction);
    }

    private void createListStatements() {
        Resource resource = getResource();
        OntModel model = getModelProvider().getOntModel();
        // Remove any previous statements associated with the list (or its
        // tails)
        Resource subset = list;
        while (subset != null && !subset.equals(RDF.nil)) {
            Resource local = subset;
            subset = subset.getPropertyResourceValue(RDF.rest);
            if (locallyCreated.contains(local)) {
                local.removeProperties();
            }
        }
        locallyCreated = Lists.newArrayList();

        Resource rest = RDF.nil;
        for (int i = members.size() - 1; i > 0; i--) {
            Resource head = model.createResource();
            locallyCreated.add(head);
            head.addProperty(RDF.first, members.get(i));
            head.addProperty(RDF.rest, rest);
            rest = head;
        }
        if (rest.equals(RDF.nil)) { // / List has been emptied
            resource.removeAll(property);
            resource.addProperty(property, RDF.nil);
        }
        else if (members.size() > 0) {
            list = model.createResource();
            locallyCreated.add(list);
            list.addProperty(RDF.first, members.get(0));
            list.addProperty(RDF.rest, rest);

            resource.removeAll(property);
            resource.addProperty(property, list);
        }

        initialMembers = new LinkedList<>(members);
        dirty = false;
        refreshProperties();
    }

    private void undo() {
        // Remove the edit history of working in the component containing the
        // list
        clearListFormOperationHistory();

        members = new LinkedList<>(initialMembers);
        dirty = false;
        refreshProperties();
    }

    private void refreshProperties() {
        updateDirtyFlag();
        createMembers();
        refresh();
    }

    private void updateDirtyFlag() {
        dirty = false;
        if (members.size() != initialMembers.size()) {
            dirty = true;
        }
        else {
            for (int i = 0; i < members.size(); i++) {
                if (!members.get(i).equals(initialMembers.get(i))) {
                    dirty = true;
                    break;
                }
            }
        }
    }

    public void setCloseListener(Listener listener) {
        this.closeListener = listener;
    }

    private void insertMember(int index, Resource member) {
        InsertMemberOperation operation = new InsertMemberOperation(index, member);
        operation.addContext(undoContext);
        try {
            operationHistory.execute(operation, null, null);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void addMember(Resource member) {
        insertMember(members.size(), member);
    }

    private void addMembers(int index, List<Resource> resources) {
        AddMembersOperation operation = new AddMembersOperation(index, resources);
        operation.addContext(undoContext);
        try {
            operationHistory.execute(operation, null, null);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void addMembers(List<Resource> resources) {
        addMembers(members.size(), resources);
    }

    private void deleteMember(int index) {
        Resource member = members.get(index);
        RemoveMemberOperation operation = new RemoveMemberOperation(index, member);
        operation.addContext(undoContext);
        try {
            operationHistory.execute(operation, null, null);
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected void createMembers() {
        if (Widgets.isNullOrDisposed(composite)) {
            return;
        }

        if (Widgets.isNullOrDisposed(membersComposite) == false) {
            membersComposite.dispose();
        }
        composite.setMenu(null); // if not set to null here, the menu could be a
                                 // disposed widget
        membersComposite = toolkit.createComposite(composite);
        membersComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                TableWrapData.TOP, 1, 1));
        {
            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 4;
            layout.verticalSpacing = 6;
            layout.horizontalSpacing = 4;
            membersComposite.setLayout(layout);
        }
        toolkit.paintBordersFor(membersComposite);

        LabelProvider labelProvider = getModelProvider().getLabelProvider();
        if (members.size() == 0) {
            toolkit.createLabel(membersComposite, "0:");

            Label iconLabel = toolkit.createLabel(membersComposite, "", SWT.NONE);
            iconLabel.setLayoutData(new TableWrapData(TableWrapData.CENTER, TableWrapData.MIDDLE,
                    1, 1));
            iconLabel.setImage(labelProvider.getImage(RDF.nil));

            Text uriText = toolkit.createText(membersComposite, labelProvider.getText(RDF.nil));
            uriText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.MIDDLE,
                    1, 1));

            toolkit.createLabel(membersComposite, "");
        }
        else {
            int index = 1;
            for (final Resource member : members) {
                toolkit.createLabel(membersComposite, "" + index + ":");

                Label iconLabel = toolkit.createLabel(membersComposite, "", SWT.NONE);
                iconLabel.setLayoutData(new TableWrapData(TableWrapData.CENTER,
                        TableWrapData.MIDDLE, 1, 1));
                iconLabel.setImage(labelProvider.getImage(member));
                iconLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseDoubleClick(MouseEvent event) {
                        CorePlugin.getDefault().openResource(member);
                    }
                });

                Text uriText = toolkit.createText(membersComposite, labelProvider.getText(member));
                uriText.setBackground(formColors.getColor(IEditorFormColors.TEXT_BG));
                uriText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                        TableWrapData.MIDDLE, 1, 1));

                ToolBar toolbar = new ToolBar(membersComposite, SWT.HORIZONTAL | SWT.FLAT);
                toolbar.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1));
                toolkit.adapt(toolbar);
                toolkit.paintBordersFor(toolbar);

                final MenuManager menuManager = new MenuManager();
                final int memberIndex = index - 1;
                Action deleteAction = new Action() {
                    @Override
                    public void run() {
                        deleteMember(memberIndex);
                    }
                };
                deleteAction.setText("Delete");
                deleteAction.setImageDescriptor(CorePlugin.getDefault().getImageDescriptor(
                        CorePluginImages.IMG_DELETE));
                menuManager.add(deleteAction);

                ToolItem menuButton = new ToolItem(toolbar, SWT.FLAT);
                menuButton.setImage(CorePlugin.getDefault().getImage(
                        CorePluginImages.IMG_ARROW_DOWN));
                menuButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Shell shell = getShell();
                        Menu menu = menuManager.createContextMenu(shell);
                        shell.setMenu(menu);
                        final ToolItem toolItem = (ToolItem) e.widget;
                        final ToolBar toolBar = toolItem.getParent();
                        Point point = toolBar.toDisplay(new Point(e.x, e.y
                                + toolItem.getBounds().height));
                        menu.setLocation(point.x, point.y);
                        menu.setVisible(true);
                    }
                });
                index++;
            }
        }
        if (dirty) {
            Composite buttonComposite = new Composite(membersComposite, SWT.NONE);
            {
                GridLayout layout = new GridLayout(2, false);
                layout.marginHeight = 0;
                layout.marginWidth = 0;
                layout.marginRight = 3;
                buttonComposite.setLayout(layout);

                TableWrapData data = new TableWrapData(TableWrapData.RIGHT, TableWrapData.TOP, 1, 4);
                buttonComposite.setLayoutData(data);
            }

            Link saveLink = new Link(buttonComposite, SWT.NONE);
            saveLink.setText("<a>Save</a>");
            saveLink.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    commit();
                }
            });

            Link cancelLink = new Link(buttonComposite, SWT.NONE);
            cancelLink.setText("<a>Cancel</a>");
            cancelLink.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    undo();
                }
            });
        }
    }

    private void clearListFormOperationHistory() {
        removeFromOperationHistory(listFormOperationsHistory, operationHistory);
        listFormOperationsHistory.clear();
        executionIndexFormOperationsHistory = 0;
    }

    private void addOperationToListFormOperationsHistory(AbstractOperation operation) {
        if (executionIndexFormOperationsHistory < 0) {
            executionIndexFormOperationsHistory = 0;
        }
        for (int i = listFormOperationsHistory.size() - 1; i >= executionIndexFormOperationsHistory; i--) {
            listFormOperationsHistory.remove(i);
        }
        listFormOperationsHistory.add(operation);
        executionIndexFormOperationsHistory = listFormOperationsHistory.size();
    }

    private void undoOperationInListFormOperationsHistory() {
        executionIndexFormOperationsHistory--;
        if (executionIndexFormOperationsHistory < 0) {
            executionIndexFormOperationsHistory = 0;
        }
    }

    private void redoOperationInListFormOperationsHistory() {
        executionIndexFormOperationsHistory++;
    }

    class InsertMemberOperation extends AbstractOperation {
        private Resource member;
        private int index;

        public InsertMemberOperation(int index, Resource member) {
            super("Insert List Item");
            this.index = index;
            this.member = member;
        }

        private void executeOperation() {
            members.add(index, member);
            refreshProperties();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            executeOperation();
            addOperationToListFormOperationsHistory(this);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            executeOperation();
            redoOperationInListFormOperationsHistory();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            members.remove(index);
            refreshProperties();
            undoOperationInListFormOperationsHistory();
            return Status.OK_STATUS;
        }
    }

    /**
     * This operation cannot be moved into a separate type file. The reason for
     * this is the fact that the operation is local to the enclosing file, and
     * needs direct access to the members of the list.
     * 
     * @author Mike Henrichs
     * 
     */
    private final class AddMembersOperation extends AbstractOperation {
        private List<Resource> resources;
        private int startIndex;

        public AddMembersOperation(int startIndex, List<Resource> resources) {
            super("Insert List Items");
            this.startIndex = startIndex;
            this.resources = resources;
        }

        private void executeOperation() {
            int index = startIndex;
            for (Resource member : resources) {
                members.add(index++, member);
            }
            refreshProperties();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            executeOperation();
            addOperationToListFormOperationsHistory(this);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            executeOperation();
            redoOperationInListFormOperationsHistory();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            int index = startIndex + resources.size() - 1;
            for (int i = 0; i < resources.size(); i++) {
                members.remove(index--);
            }
            refreshProperties();
            undoOperationInListFormOperationsHistory();
            return Status.OK_STATUS;
        }
    }

    /**
     * This operation cannot be moved into a separate type file. The reason for
     * this is the fact that the operation is local to the enclosing file, and
     * needs direct access to the members of the list.
     * 
     * @author Mike Henrichs
     * 
     */
    private final class RemoveMemberOperation extends AbstractOperation {
        private Resource member;
        private int index;

        public RemoveMemberOperation(int index, Resource member) {
            super("Remove List Item");
            this.index = index;
            this.member = member;
        }

        private void executeOperation() {
            members.remove(index);
            refreshProperties();
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            executeOperation();
            addOperationToListFormOperationsHistory(this);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            executeOperation();
            redoOperationInListFormOperationsHistory();
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            members.add(index, member);
            refreshProperties();
            undoOperationInListFormOperationsHistory();
            return Status.OK_STATUS;
        }
    }

    @Override
    public void refresh() {
        super.refresh();
    }
}
