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

package com.semmtech.plugin.semmweb.editor.views.triples;


import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dnd.ResourceArrayListTransfer;
import com.semmtech.plugin.semmweb.core.dnd.ResourceTransfer;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;
import com.semmtech.plugin.semmweb.core.viewers.ResourceViewerToolTipSupport;
import com.semmtech.plugin.semmweb.core.widgets.SearchComposite;
import com.semmtech.plugin.semmweb.core.widgets.SearchFilterChangedListener;
import com.semmtech.plugin.semmweb.editor.EditorPlugin;
import com.semmtech.plugin.semmweb.editor.EditorPluginImages;
import com.semmtech.ui.plugin.util.FontUtil;
import com.semmtech.ui.plugin.widgets.Widgets;


public class ResourceSelector extends Composite {

    private CheckboxTableViewer viewer;
    private boolean initializing;

    private final List<IFilterChangedListener> listeners;
    private Table table;
    private SearchComposite searchField;
    private final Font boldFont;
    private String filter;
    private StyledCellLabelProvider labelProvider;
    private Collection<? extends RDFNode> input;
    private final List<RDFNode> checkedNodes;
    private List<?> shownNodes;
    private Label statusLabel;
    private Composite container;

    public ResourceSelector(Composite parent, int style) {
        super(parent, style);

        boldFont = FontUtil.getModifiedFont(getParent(), SWT.BOLD);
        checkedNodes = Lists.newArrayList();
        listeners = Lists.newArrayList();

        createControl();
    }

    @Override
    public void dispose() {
        boldFont.dispose();
    }

    private void createControl() {
        initializing = true;

        setLayout(new FillLayout());

        container = new Composite(this, SWT.NONE);

        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        container.setLayout(layout);

        searchField = new SearchComposite(container, SWT.NONE);
        searchField.addSearchFilterChangedListener(new SearchFilterChangedListener() {

            @Override
            public void filterChanged(String value) {
                filter = value;
                refreshViewer();
            }
        });
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);

        searchField.setLayoutData(layoutData);

        Composite inner = new Composite(container, SWT.BORDER);
        layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        inner.setLayout(layout);
        inner.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

        ToolBar toolBar = new ToolBar(inner, SWT.HORIZONTAL | SWT.FLAT | SWT.RIGHT);
        layoutData = new GridData(GridData.END, GridData.CENTER, true, false);
        layoutData.minimumHeight = 30;
        toolBar.setLayoutData(layoutData);

        ToolItem checkNoneItem = new ToolItem(toolBar, SWT.PUSH);
        checkNoneItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_CHECK_NONE));
        checkNoneItem.setToolTipText("Uncheck all items shown");
        checkNoneItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                uncheckAllViewed();
                fireFilterChanged();
            }
        });

        ToolItem checkAllItem = new ToolItem(toolBar, SWT.PUSH);
        checkAllItem.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_CHECK_ALL));
        checkAllItem.setToolTipText("Check all items shown");
        checkAllItem.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                checkAllViewed();
                fireFilterChanged();
            }
        });

        table = new Table(inner, SWT.CHECK | SWT.VIRTUAL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        labelProvider = new StyledCellLabelProvider() {
            private final Styler filterStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.font = boldFont;
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
                }
            };

            private final Styler localStyler = new Styler() {
                @Override
                public void applyStyles(TextStyle textStyle) {
                    textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
                }
            };

            @Override
            public void update(ViewerCell cell) {
                Object element = cell.getElement();
                StyledString styledText = new StyledString();
                String text = getElementText(element);

                if (text != null && text.length() > 0) {
                    styledText.append(text);
                }

                if (text != null && !Strings.isNullOrEmpty(filter) && !filter.equals("*")) {
                    int startIndex = text.toLowerCase().indexOf(filter.toLowerCase());
                    while (startIndex > -1) {
                        int length = filter.length();
                        styledText.setStyle(startIndex, length, filterStyler);
                        startIndex = text.toLowerCase().indexOf(filter.toLowerCase(),
                                startIndex + 1);
                    }
                }
                if (text != null
                        && CorePlugin.getDefault().getActiveModelProvider() != null
                        && text.equals(CorePlugin.getDefault().getActiveModelProvider()
                                .getModelTitle())) {
                    styledText.setStyle(0, styledText.getString().length(), localStyler);
                }

                cell.setImage(getElementImage(element));
                cell.setText(styledText.getString());
                cell.setStyleRanges(styledText.getStyleRanges());
                super.update(cell);
            }

            public String getElementText(Object element) {
                return getLabelProvider().getText(element);
            }

            public Image getElementImage(Object element) {
                if (element instanceof String) {
                    return CorePlugin.getDefault().getImage(CorePluginImages.IMG_OWL_ONTOLOGY);
                }
                return getLabelProvider().getImage(element);
            }

            @Override
            public int getToolTipDisplayDelayTime(Object object) {
                return 80;
            }

            @Override
            public int getToolTipTimeDisplayed(Object object) {
                return 10000;
            }

            @Override
            public Point getToolTipShift(Object object) {
                return new Point(8, 10);
            }

            @Override
            public String getToolTipText(Object element) {
                if (element instanceof Resource) {
                    return ((Resource) element).toString();
                }
                return null;
            }
        };

        viewer = new CheckboxTableViewer(table);
        viewer.setContentProvider(new ILazyContentProvider() {
            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer v, Object oldInput, Object newInput) {
                if (!(newInput instanceof List<?>)) {
                    shownNodes = null;
                    viewer.setItemCount(0);
                }
                else if (Strings.isNullOrEmpty(filter) || filter.equals("*")) {
                    shownNodes = (List<?>) newInput;
                    int itemCount = shownNodes.size();
                    viewer.setItemCount(itemCount);
                    updateStatus(itemCount, checkedNodes.size());
                }
                else {
                    List<?> collection = (List<?>) newInput;
                    List<RDFNode> nodes = Lists.newArrayListWithCapacity(collection.size());
                    LabelProvider labelProvider = getLabelProvider();
                    for (Object object : collection) {
                        if (!(object instanceof RDFNode)) {
                            continue;
                        }
                        RDFNode node = (RDFNode) object;
                        String text = labelProvider.getText(node);
                        if (text.toLowerCase().contains(filter.toLowerCase())) {
                            nodes.add(node);
                        }
                    }
                    shownNodes = nodes;
                    int itemCount = shownNodes.size();
                    viewer.setItemCount(itemCount);
                    updateStatus(collection.size(), itemCount, checkedNodes.size());
                }
            }

            @Override
            public void updateElement(int index) {
                Object element = shownNodes.get(index);
                viewer.replace(element, index);
                // viewer.setChecked(element, checkedNodes.contains(element));
                viewer.getTable().getItems()[index].setChecked(checkedNodes.contains(element));
            }
        });
        viewer.setUseHashlookup(true);

        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        viewer.addDropSupport(operations, new Transfer[] { ResourceArrayListTransfer.getInstance(),
                ResourceTransfer.getInstance() }, new DropTargetAdapter() {

            @Override
            public void drop(DropTargetEvent event) {
                if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    Resource resource = (Resource) ResourceTransfer.getInstance().nativeToJava(
                            event.currentDataType);
                    if (event.detail == DND.DROP_MOVE) {
                        uncheckAll();
                    }
                    if (!checkedNodes.contains(resource)) {
                        checkedNodes.add(resource);
                        viewer.setChecked(resource, true);
                    }
                }
                else if (ResourceArrayListTransfer.getInstance().isSupportedType(
                        event.currentDataType)) {
                    ResourceArrayList list = (ResourceArrayList) ResourceArrayListTransfer
                            .getInstance().nativeToJava(event.currentDataType);
                    if (event.detail == DND.DROP_MOVE) {
                        uncheckAll();
                    }
                    for (Resource resource : list) {
                        if (!checkedNodes.contains(resource)) {
                            checkedNodes.add(resource);
                            viewer.setChecked(resource, true);
                        }
                    }
                }
            }
        });
        viewer.setLabelProvider(labelProvider);
        viewer.setCheckStateProvider(new ICheckStateProvider() {
            @Override
            public boolean isChecked(Object element) {
                return checkedNodes.contains(element);
            }

            @Override
            public boolean isGrayed(Object element) {
                return false;
            }
        });
        viewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                if (!(event.getElement() instanceof RDFNode)) {
                    return;
                }
                RDFNode node = (RDFNode) event.getElement();
                if (!checkedNodes.contains(node)) {
                    checkedNodes.add(node);
                    viewer.setChecked(node, true);
                    updateStatus();
                }
                else {
                    checkedNodes.remove(node);
                    viewer.setChecked(node, false);
                    updateStatus();
                }
                fireFilterChanged();
            }

        });

        table.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                // fireFilterChanged();
            }

        });
        ResourceViewerToolTipSupport.enableFor(viewer);
        createContextMenu();

        statusLabel = new Label(container, SWT.NONE);
        statusLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER, true, false, 2, 1));

        initializing = false;
    }

    private void checkAll() {
        /*
         * Should only check/uncheck TableItems that already have content.
         * Otherwise, the ILazyContentProvider will consider those empty
         * TableItems already set/filled with content, even though they may not
         * be!
         */
        for (TableItem item : table.getItems()) {
            Object element = item.getData();
            if (element != null) {
                item.setChecked(true);
            }
        }
        checkedNodes.clear();
        checkedNodes.addAll(Lists.newArrayList(input));
    }

    private void uncheckAll() {
        /*
         * Should only check/uncheck TableItems that already have content.
         * Otherwise, the ILazyContentProvider will consider those empty
         * TableItems already set/filled with content, even though they may not
         * be!
         */
        for (TableItem item : table.getItems()) {
            Object element = item.getData();
            if (element != null) {
                item.setChecked(false);
            }
        }
        checkedNodes.clear();
    }

    private void checkOrUncheckAllViewed(boolean check) {
        for (Object n : shownNodes) {
            if (n instanceof RDFNode) {
                RDFNode node = (RDFNode) n;
                if (checkedNodes.contains(node)) {
                    // Uncheck node item
                    checkedNodes.remove(node);
                    viewer.setChecked(node, false);
                }
                else {
                    // Check node item
                    checkedNodes.add(node);
                    viewer.setChecked(node, true);
                }
            }
        }

    }

    private void checkAllViewed() {
        if (Strings.isNullOrEmpty(filter) || filter.equals("*")) {
            checkAll();
        }
        else {
            checkOrUncheckAllViewed(true);
        }
        updateStatus();
    }

    private void uncheckAllViewed() {
        if (Strings.isNullOrEmpty(filter) || filter.equals("*")) {
            uncheckAll();
        }
        else {
            checkOrUncheckAllViewed(false);
        }
        updateStatus();
    }

    private void createContextMenu() {
        MenuManager menuManager = new MenuManager();
        menuManager.addMenuListener(new IMenuListener() {
            private final ImageDescriptor checkAllImage = EditorPlugin.getDefault()
                    .getImageDescriptor(EditorPluginImages.IMG_SELECTION_CHECK_ALL);
            private final ImageDescriptor checkNoneImage = EditorPlugin.getDefault()
                    .getImageDescriptor(EditorPluginImages.IMG_SELECTION_CHECK_NONE);

            @Override
            public void menuAboutToShow(IMenuManager manager) {
                IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
                if (selection.isEmpty()) {
                    return;
                }

                manager.add(new Action() {
                    @Override
                    public String getText() {
                        return "Check";
                    }

                    @Override
                    public ImageDescriptor getImageDescriptor() {
                        return checkAllImage;
                    }

                    @Override
                    public void run() {
                        IStructuredSelection selection = (IStructuredSelection) viewer
                                .getSelection();
                        for (Object element : selection.toArray()) {
                            RDFNode node = (RDFNode) element;
                            viewer.setChecked(node, true);
                            if (!checkedNodes.contains(node)) {
                                checkedNodes.add(node);
                            }
                        }
                    }
                });
                manager.add(new Action() {
                    @Override
                    public String getText() {
                        return "Uncheck";
                    }

                    @Override
                    public ImageDescriptor getImageDescriptor() {
                        return checkNoneImage;
                    }

                    @Override
                    public void run() {
                        IStructuredSelection selection = (IStructuredSelection) viewer
                                .getSelection();
                        for (Object element : selection.toArray()) {
                            RDFNode node = (RDFNode) element;
                            viewer.setChecked(node, false);
                            if (checkedNodes.contains(node)) {
                                checkedNodes.remove(node);
                            }
                        }
                    }
                });
            }
        });
        menuManager.setRemoveAllWhenShown(true);
        Menu menu = menuManager.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);

    }

    private void updateStatus() {
        if (viewer.getTable().getItemCount() < input.size()) {
            updateStatus(input.size(), viewer.getTable().getItemCount(), checkedNodes.size());
        }
        else {
            updateStatus(input.size(), checkedNodes.size());
        }
    }

    private void updateStatus(int itemsTotal, int itemsShown, int itemsSelected) {
        String message = String.format("%s total (%s shown, %s selected)", itemsTotal, itemsShown,
                itemsSelected);
        updateStatus(message);
    }

    private void updateStatus(int itemsTotal, int itemsSelected) {
        String message = String.format("%s total (%s selected)", itemsTotal, itemsSelected);
        updateStatus(message);
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        container.layout(true, true);
    }

    private LabelProvider getLabelProvider() {
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        if (provider != null) {
            return provider.getLabelProvider();
        }
        return null;
    }

    public void reset() {
        searchField.setFilter("");
        filter = "";

        checkedNodes.clear();
        checkedNodes.addAll(Lists.newArrayList(input));
        refreshViewer();
    }

    protected void refreshViewer() {
        if (!Widgets.isNullOrDisposedViewer(viewer)) {
            viewer.setInput(input);
        }
    }

    private void fireFilterChanged() {
        if (initializing) {
            return;
        }
        for (IFilterChangedListener listener : listeners) {
            listener.filterChanged();
        }
    }

    public void setSelection(List<RDFNode> selected) {
        checkedNodes.clear();
        checkedNodes.addAll(selected);
        if (!Widgets.isNullOrDisposedViewer(viewer)) {
            viewer.refresh();
        }
    }

    public List<RDFNode> getSelectedNodes() {
        return checkedNodes;
    }

    public boolean isNoneSelected() {
        return checkedNodes.isEmpty();
    }

    public boolean isAllSelected() {
        return checkedNodes.size() == input.size();
    }

    public void setInput(Collection<? extends RDFNode> input) {
        if (input == null) {
            input = Lists.newArrayList();
        }
        this.input = input;
        checkedNodes.clear();
        checkedNodes.addAll(Lists.newArrayList(input));
        if (!Widgets.isNullOrDisposedViewer(viewer)) {
            viewer.setInput(input);
        }
    }

    public void addFilterChangedListener(IFilterChangedListener listener) {
        listeners.add(listener);
    }

    public void removeFilterChangedListener(IFilterChangedListener listener) {
        listeners.remove(listener);
    }

    public boolean isSelected(RDFNode node) {
        return viewer.getChecked(node);
    }
}
